package com.vizorix.api.engine;

import java.util.List;

/**
 * Immutable result object produced by the execution engine after a full program trace.
 *
 * <p>This record is the boundary between the execution engine layer and the persistence/REST
 * layers. It must never contain JPA entities or Spring components.
 *
 * <p>{@code ExecutionPersistenceService} converts this to JPA entities. {@code ExecutionMapper}
 * converts individual steps to REST DTOs.
 */
public record ExecutionResult(
    List<ExecutionStepData> steps,
    long compileTimeMs,
    long executionTimeMs,
    boolean succeeded,
    String errorMessage) {}
