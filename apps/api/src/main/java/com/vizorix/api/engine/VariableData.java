package com.vizorix.api.engine;

/**
 * Immutable snapshot of a single variable captured at an execution step.
 *
 * <p>Engine-layer record only — no JPA. Maps to {@code VariableSnapshot} entity via the persistence
 * service.
 */
public record VariableData(String name, String value, String type, String scope) {}
