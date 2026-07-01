# Execution Engine Package (`packages/execution-engine`)

## Purpose
Compiles and executes user-provided Java programs safely inside secure sandboxed environments to collect raw execution logs.

## Responsibility
- Compiles source code inputs (e.g. `.java` classes).
- Runs code within a constrained sandbox, restricting network access and system calls.
- Traces execution states (variables, call stack, heap updates) and streams trace logs.

## Planned Public Interfaces
- `compile(sourceFiles: Map<string, string>): Promise<CompilationOutput>`
- `execute(binaryPayload: ArrayBuffer, runConfig: RunConfig): Promise<TraceStream>`

## Dependencies
- None
