package com.vizorix.api.engine;

/**
 * Immutable snapshot of a single call stack frame captured at an execution step.
 *
 * <p>Engine-layer record — returned in-memory, not persisted for M10.
 */
public record StackFrameData(String className, String methodName, int lineNumber) {}
