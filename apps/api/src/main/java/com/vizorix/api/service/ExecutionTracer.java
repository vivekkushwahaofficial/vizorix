package com.vizorix.api.service;

import com.vizorix.api.engine.ExecutionResult;
import com.vizorix.api.exception.ExecutionEngineException;
import java.nio.file.Path;

/**
 * Contract for stepping through a compiled program and collecting execution traces.
 *
 * <p>Implementations may use JDI/JDWP, bytecode instrumentation, JavaParser, or a custom
 * interpreter. Swapping the tracing strategy requires only a new implementation — {@code
 * ExecutionService} and all callers remain unchanged.
 */
public interface ExecutionTracer {

  /**
   * Traces the execution of the given compiled class, collecting a snapshot at each step.
   *
   * @param classDir the directory containing the compiled {@code .class} files
   * @param mainClass the fully-qualified main class name to execute
   * @param timeoutSeconds maximum execution time before the subprocess is forcibly terminated
   * @return an {@link ExecutionResult} containing all traced steps and timing metadata
   * @throws ExecutionEngineException if the trace process fails to launch or encounters a fatal
   *     error
   */
  ExecutionResult trace(Path classDir, String mainClass, int timeoutSeconds)
      throws ExecutionEngineException;
}
