package com.vizorix.api.mapper;

import com.vizorix.api.domain.ExecutionSession;
import com.vizorix.api.domain.ExecutionStep;
import com.vizorix.api.domain.Project;
import com.vizorix.api.domain.VariableSnapshot;
import com.vizorix.api.domain.enums.SessionStatus;
import com.vizorix.api.domain.enums.VariableScope;
import com.vizorix.api.dto.ExecutionSessionSummaryResponse;
import com.vizorix.api.dto.ExecutionStepResponse;
import com.vizorix.api.dto.StackFrameDto;
import com.vizorix.api.dto.VariableSnapshotDto;
import com.vizorix.api.engine.ExecutionResult;
import com.vizorix.api.engine.ExecutionStepData;
import com.vizorix.api.engine.StackFrameData;
import com.vizorix.api.engine.VariableData;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** Spring Component mapping between engine models, database entities, and REST DTOs. */
@Component
public class ExecutionMapper {

  public ExecutionSession toSessionEntity(ExecutionResult result, Project project) {
    if (result == null) {
      return null;
    }
    ExecutionSession session = new ExecutionSession();
    session.setProject(project);
    if (result.succeeded()) {
      session.setStatus(SessionStatus.SUCCESS);
    } else if ("Cancelled by user".equals(result.errorMessage())) {
      session.setStatus(SessionStatus.CANCELLED);
    } else {
      session.setStatus(SessionStatus.FAILED);
    }
    session.setErrorMessage(result.errorMessage());
    session.setCompileTimeMs(result.compileTimeMs());
    session.setExecutionTimeMs(result.executionTimeMs());
    session.setTotalSteps(result.steps().size());
    session.setTotalVariables(result.steps().stream().mapToInt(s -> s.variables().size()).sum());
    session.setTotalConsoleLines(
        result.steps().isEmpty()
            ? 0
            : result.steps().get(result.steps().size() - 1).stdout().split("\n", -1).length);
    session.setMaxStackDepth(
        result.steps().stream().mapToInt(s -> s.callStack().size()).max().orElse(0));

    List<ExecutionStep> steps = new ArrayList<>();
    for (ExecutionStepData stepData : result.steps()) {
      steps.add(toStepEntity(stepData, session));
    }
    session.setSteps(steps);
    return session;
  }

  public ExecutionStep toStepEntity(ExecutionStepData data, ExecutionSession session) {
    if (data == null) {
      return null;
    }
    ExecutionStep step = new ExecutionStep();
    step.setSession(session);
    step.setStepNumber(data.stepNumber());
    step.setLineNumber(data.lineNumber());
    step.setClassName(data.className());
    step.setMethodName(data.methodName());
    step.setStdout(data.stdout());
    step.setStderr(data.stderr());

    List<VariableSnapshot> snapshots = new ArrayList<>();
    for (VariableData varData : data.variables()) {
      snapshots.add(toVariableEntity(varData, step));
    }
    step.setSnapshots(snapshots);
    return step;
  }

  public VariableSnapshot toVariableEntity(VariableData data, ExecutionStep step) {
    if (data == null) {
      return null;
    }
    VariableSnapshot snapshot = new VariableSnapshot();
    snapshot.setStep(step);
    snapshot.setName(data.name());
    snapshot.setValue(data.value());
    snapshot.setType(data.type());
    try {
      snapshot.setScope(VariableScope.valueOf(data.scope()));
    } catch (IllegalArgumentException | NullPointerException e) {
      snapshot.setScope(VariableScope.LOCAL);
    }
    return snapshot;
  }

  public ExecutionSessionSummaryResponse toSummaryResponse(ExecutionSession session) {
    if (session == null) {
      return null;
    }
    ExecutionSessionSummaryResponse response = new ExecutionSessionSummaryResponse();
    response.setExecutionId(session.getId());
    response.setTotalSteps(session.getTotalSteps() != null ? session.getTotalSteps() : 0);
    response.setStatus(session.getStatus().name());
    response.setErrorMessage(session.getErrorMessage());
    response.setCompileTimeMs(session.getCompileTimeMs());
    response.setExecutionTimeMs(session.getExecutionTimeMs());
    return response;
  }

  public ExecutionStepResponse toStepResponse(
      ExecutionStep step, int totalSteps, List<StackFrameData> callStack) {
    if (step == null) {
      return null;
    }
    ExecutionStepResponse response = new ExecutionStepResponse();
    response.setStep(step.getStepNumber());
    response.setTotalSteps(totalSteps);
    response.setLineNumber(step.getLineNumber());
    response.setClassName(step.getClassName());
    response.setMethodName(step.getMethodName());
    response.setStdout(step.getStdout());
    response.setStderr(step.getStderr());
    response.setVariables(
        step.getSnapshots().stream().map(this::toVariableDto).collect(Collectors.toList()));
    response.setCallStack(
        callStack.stream().map(this::toStackFrameDto).collect(Collectors.toList()));
    return response;
  }

  public VariableSnapshotDto toVariableDto(VariableSnapshot entity) {
    if (entity == null) {
      return null;
    }
    VariableSnapshotDto dto = new VariableSnapshotDto();
    dto.setName(entity.getName());
    dto.setValue(entity.getValue());
    dto.setType(entity.getType());
    dto.setScope(entity.getScope().name());
    return dto;
  }

  public StackFrameDto toStackFrameDto(StackFrameData data) {
    if (data == null) {
      return null;
    }
    StackFrameDto dto = new StackFrameDto();
    dto.setClassName(data.className());
    dto.setMethodName(data.methodName());
    dto.setLineNumber(data.lineNumber());
    return dto;
  }
}
