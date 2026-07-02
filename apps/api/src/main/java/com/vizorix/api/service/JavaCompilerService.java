package com.vizorix.api.service;

import com.vizorix.api.engine.CompilationResult;
import com.vizorix.api.exception.CompilationException;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Compiles Java source code using the JDK's built-in {@code javax.tools.JavaCompiler}.
 * Automatically detects the public class name and checks for a valid main entry point.
 */
@Service
public class JavaCompilerService implements CompilerService {

  private static final Logger log = LoggerFactory.getLogger(JavaCompilerService.class);

  @Override
  public CompilationResult compile(String sourceCode, Path outputDir) throws CompilationException {
    String className = detectClassName(sourceCode);

    // Validate main method presence before compiling to save execution tracing failures
    if (!hasMainMethod(sourceCode)) {
      throw new CompilationException(
          "Source code verification failed",
          List.of(
              "Error: No public static void main(String[] args) method found in class "
                  + className
                  + "."));
    }

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    if (compiler == null) {
      throw new CompilationException(
          "Java compiler not available — ensure the application runs on a JDK, not a JRE",
          List.of());
    }

    try {
      Path sourceFile = outputDir.resolve(className + ".java");
      Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8);

      DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
      StringWriter out = new StringWriter();

      try (var fileManager =
          compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
        var compilationUnits = fileManager.getJavaFileObjects(sourceFile.toFile());

        var task =
            compiler.getTask(
                out,
                fileManager,
                diagnostics,
                List.of(
                    "-d",
                    outputDir.toString(),
                    "-g"), // -g emits debug info for JDI local variables
                null,
                compilationUnits);

        boolean success = task.call();

        if (!success) {
          List<String> messages = new ArrayList<>();
          for (Diagnostic<? extends JavaFileObject> d : diagnostics.getDiagnostics()) {
            if (d.getKind() == Diagnostic.Kind.ERROR) {
              messages.add(
                  String.format("Line %d: %s", d.getLineNumber(), d.getMessage(Locale.ROOT)));
            }
          }
          log.warn("Compilation failed with {} errors", messages.size());
          throw new CompilationException("Compilation failed", messages);
        }
      }

      log.debug("Compiled {}.java to {}", className, outputDir);
      return new CompilationResult(outputDir, className);

    } catch (IOException e) {
      throw new CompilationException("Failed to write source file: " + e.getMessage(), List.of());
    }
  }

  @Override
  public String language() {
    return "JAVA";
  }

  private String detectClassName(String sourceCode) {
    var matcher = Pattern.compile("public\\s+class\\s+([A-Za-z0-9_$]+)").matcher(sourceCode);
    if (matcher.find()) {
      return matcher.group(1);
    }
    var classMatcher = Pattern.compile("class\\s+([A-Za-z0-9_$]+)").matcher(sourceCode);
    if (classMatcher.find()) {
      return classMatcher.group(1);
    }
    return "Main";
  }

  private boolean hasMainMethod(String sourceCode) {
    var pattern =
        Pattern.compile(
            "public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*(\\[\\s*\\]\\s*\\w+|\\w+\\s*\\[\\s*\\])\\s*\\)");
    return pattern.matcher(sourceCode).find();
  }
}
