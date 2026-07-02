package com.vizorix.api.exception;

import java.util.List;

/** Exception thrown when Java source code fails to compile. */
public class CompilationException extends ExecutionEngineException {

  private final List<String> diagnostics;

  /**
   * Constructs the compilation exception.
   *
   * @param message summary error description
   * @param diagnostics list of compiler diagnostic messages
   */
  public CompilationException(String message, List<String> diagnostics) {
    super(message);
    this.diagnostics = List.copyOf(diagnostics);
  }

  /**
   * Returns the compiler diagnostics collected during the failed compilation.
   *
   * @return immutable list of diagnostic messages
   */
  public List<String> getDiagnostics() {
    return diagnostics;
  }
}
