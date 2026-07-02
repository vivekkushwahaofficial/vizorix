package com.vizorix.api.service;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Location;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;
import com.vizorix.api.engine.ExecutionResult;
import com.vizorix.api.engine.ExecutionStepData;
import com.vizorix.api.engine.StackFrameData;
import com.vizorix.api.engine.VariableData;
import com.vizorix.api.exception.ExecutionEngineException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Traces Java program execution using the Java Debug Interface (JDI) over JDWP.
 *
 * <p>Launches the compiled class in a subprocess with a JDWP agent on a random port, attaches via
 * JDI, then steps through every source line collecting variables and call stack at each step.
 *
 * <p>The subprocess is killed if it exceeds {@code timeoutSeconds} or if {@link #cancel()} is
 * called.
 */
@Service
public class JdiExecutionTracer implements ExecutionTracer {

  private static final Logger log = LoggerFactory.getLogger(JdiExecutionTracer.class);
  private static final int MAX_STEPS = 10_000; // guard against infinite loops

  /** Shared cancellation flag — set by cancel() to kill the subprocess early. */
  private final AtomicBoolean cancelled = new AtomicBoolean(false);

  private volatile Process subprocess;

  /** Signals the tracer to kill the current subprocess on the next step event. */
  public void cancel() {
    cancelled.set(true);
    Process p = subprocess;
    if (p != null && p.isAlive()) {
      p.destroyForcibly();
    }
  }

  @Override
  public ExecutionResult trace(Path classDir, String mainClass, int timeoutSeconds)
      throws ExecutionEngineException {

    cancelled.set(false);
    long startTime = System.currentTimeMillis();
    List<ExecutionStepData> steps = new ArrayList<>();

    // ── 1. Launch subprocess with JDWP on a random available port ──────────────
    int debugPort = findFreePort();
    ProcessBuilder pb =
        new ProcessBuilder(
            "java",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=" + debugPort,
            "-cp",
            classDir.toString(),
            mainClass);
    pb.redirectErrorStream(false);

    try {
      subprocess = pb.start();
    } catch (IOException e) {
      throw new ExecutionEngineException("Failed to launch subprocess: " + e.getMessage(), e);
    }

    // ── 2. Attach to the subprocess via JDI ────────────────────────────────────
    VirtualMachine vm;
    try {
      vm = attachJdi(debugPort, timeoutSeconds);
    } catch (Exception e) {
      subprocess.destroyForcibly();
      throw new ExecutionEngineException("Failed to attach JDI: " + e.getMessage(), e);
    }

    // ── 3. Request step events for the main thread ─────────────────────────────
    EventRequestManager erm = vm.eventRequestManager();
    StringBuilder stdoutBuffer = new StringBuilder();
    StringBuilder stderrBuffer = new StringBuilder();

    // Start I/O capture threads
    Thread stdoutThread =
        new Thread(() -> captureStream(subprocess.getInputStream(), stdoutBuffer));
    Thread stderrThread =
        new Thread(() -> captureStream(subprocess.getErrorStream(), stderrBuffer));
    stdoutThread.setDaemon(true);
    stderrThread.setDaemon(true);
    stdoutThread.start();
    stderrThread.start();

    int stepCount = 0;

    try {
      // Wait for the main class to be prepared, then set a step request
      EventQueue eventQueue = vm.eventQueue();
      boolean running = true;

      // Request class prepare events so we can set step request after class loads
      vm.eventRequestManager().createClassPrepareRequest().enable();
      vm.resume();

      while (running && stepCount < MAX_STEPS && !cancelled.get()) {
        // Check timeout
        long elapsed = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsed >= timeoutSeconds) {
          log.warn("Execution timed out after {}s", timeoutSeconds);
          break;
        }

        EventSet eventSet;
        try {
          eventSet = eventQueue.remove(500); // 500ms poll
          if (eventSet == null) {
            continue;
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }

        for (Event event : eventSet) {
          if (event instanceof ClassPrepareEvent cpe) {
            // Set step request on the main thread when Main class loads
            if (cpe.referenceType().name().equals(mainClass)) {
              ThreadReference mainThread = cpe.thread();
              StepRequest stepRequest =
                  erm.createStepRequest(mainThread, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
              stepRequest.enable();
            }

          } else if (event instanceof StepEvent se) {
            stepCount++;
            ExecutionStepData stepData = collectStep(se, stepCount, stdoutBuffer, stderrBuffer);
            steps.add(stepData);

          } else if (event instanceof BreakpointEvent) {
            // No breakpoints set; defensive branch
          } else if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
            running = false;
          }
        }

        eventSet.resume();
      }

    } catch (Exception e) {
      log.error("JDI tracing error: {}", e.getMessage(), e);
    } finally {
      try {
        vm.exit(0);
      } catch (Exception ignored) {
      }
      subprocess.destroyForcibly();
    }

    long executionTimeMs = System.currentTimeMillis() - startTime;
    boolean succeeded = !cancelled.get() && stepCount < MAX_STEPS;

    return new ExecutionResult(
        steps,
        0L, // compileTimeMs is set by ExecutionService (before this call)
        executionTimeMs,
        succeeded,
        cancelled.get() ? "Cancelled by user" : null);
  }

  // ── Private helpers ─────────────────────────────────────────────────────────

  private ExecutionStepData collectStep(
      StepEvent se, int stepNumber, StringBuilder stdout, StringBuilder stderr) {

    Location location = se.location();
    ThreadReference thread = se.thread();

    List<VariableData> variables = new ArrayList<>();
    List<StackFrameData> callStack = new ArrayList<>();

    try {
      // Collect local variables from the top frame
      StackFrame topFrame = thread.frame(0);
      try {
        List<LocalVariable> visibleVars = topFrame.visibleVariables();
        for (LocalVariable lv : visibleVars) {
          Value val = topFrame.getValue(lv);
          String valueStr = val != null ? val.toString() : "null";
          variables.add(new VariableData(lv.name(), valueStr, lv.typeName(), "LOCAL"));
        }
      } catch (AbsentInformationException e) {
        // Compiled without -g or class doesn't expose debug info — skip variables
        log.trace("No variable info at step {}: {}", stepNumber, e.getMessage());
      }

      // Collect call stack from all frames
      for (StackFrame frame : thread.frames()) {
        Location loc = frame.location();
        callStack.add(
            new StackFrameData(loc.declaringType().name(), loc.method().name(), loc.lineNumber()));
      }

    } catch (Exception e) {
      log.trace("Error collecting step {} data: {}", stepNumber, e.getMessage());
    }

    // Snapshot stdout/stderr at this step (accumulated since start)
    String stdoutSnapshot = stdout.toString();
    String stderrSnapshot = stderr.toString();

    return new ExecutionStepData(
        stepNumber,
        location.lineNumber(),
        location.declaringType().name(),
        location.method().name(),
        stdoutSnapshot,
        stderrSnapshot,
        variables,
        callStack);
  }

  private VirtualMachine attachJdi(int port, int timeoutSeconds)
      throws IOException, IllegalConnectorArgumentsException {

    AttachingConnector connector =
        Bootstrap.virtualMachineManager().attachingConnectors().stream()
            .filter(c -> c.name().equals("com.sun.jdi.SocketAttach"))
            .findFirst()
            .orElseThrow(() -> new ExecutionEngineException("SocketAttach connector not found"));

    Map<String, Connector.Argument> args = connector.defaultArguments();
    args.get("hostname").setValue("localhost");
    args.get("port").setValue(String.valueOf(port));
    args.get("timeout").setValue(String.valueOf(timeoutSeconds * 1000));

    // Retry attach with backoff — subprocess may not have opened the socket yet
    int retries = 20;
    while (retries-- > 0) {
      try {
        return connector.attach(args);
      } catch (IOException e) {
        if (retries == 0) {
          throw e;
        }
        try {
          Thread.sleep(200);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
        }
      }
    }
    throw new ExecutionEngineException("Timed out waiting for JDI socket on port " + port);
  }

  private int findFreePort() {
    try (var socket = new java.net.ServerSocket(0)) {
      return socket.getLocalPort();
    } catch (IOException e) {
      throw new ExecutionEngineException("Could not find a free port for JDWP", e);
    }
  }

  private void captureStream(java.io.InputStream stream, StringBuilder buffer) {
    try (var reader = new java.io.BufferedReader(new java.io.InputStreamReader(stream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append('\n');
      }
    } catch (IOException ignored) {
    }
  }
}
