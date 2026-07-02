package com.vizorix.api.service;

import com.vizorix.api.exception.CompilationException;
import java.nio.file.Path;

/**
 * Contract for language-specific source code compilation.
 *
 * <p>Implementations compile a source code string to a class output directory. When additional
 * languages are added (Python, C++, JavaScript), each gets its own implementation without changing
 * {@code ExecutionService}.
 */
public interface CompilerService {

  /**
   * Compiles the given source code and writes output artifacts to the specified directory.
   *
   * @param sourceCode the raw source code string to compile
   * @param outputDir the directory to write compiled artifacts into
   * @return path to the main compiled artifact or class directory
   * @throws CompilationException if compilation fails; carries diagnostic messages
   */
  com.vizorix.api.engine.CompilationResult compile(String sourceCode, Path outputDir)
      throws CompilationException;

  /**
   * Returns the language identifier this compiler handles.
   *
   * @return language string (e.g. {@code "JAVA"})
   */
  String language();
}
