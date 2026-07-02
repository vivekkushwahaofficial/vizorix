package com.vizorix.api.engine;

import java.nio.file.Path;

/** Record containing the result of a compilation pass. */
public record CompilationResult(Path outputDir, String mainClassName) {}
