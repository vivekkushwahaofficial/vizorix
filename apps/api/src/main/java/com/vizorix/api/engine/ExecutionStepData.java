package com.vizorix.api.engine;

import java.util.List;

/**
 * Immutable snapshot of a single execution step produced by the tracer.
 *
 * <p>This is a pure engine record — no JPA annotations, no Spring context. The persistence layer
 * maps it to {@code ExecutionStep} + {@code VariableSnapshot} entities.
 */
public record ExecutionStepData(
    int stepNumber,
    int lineNumber,
    String className,
    String methodName,
    String stdout,
    String stderr,
    List<VariableData> variables,
    List<StackFrameData> callStack) {}
