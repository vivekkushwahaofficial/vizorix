# ADR 0003: Java Language Focus

## Status
Approved

## Context
While Vizorix is planned to support multiple languages (Java, Python, C++, JavaScript), building compilers, debuggers, and visual trace frameworks for all of them simultaneously in the MVP stage would stretch resources and delay feedback loops.

## Decision
We will design the execution interfaces, memory model models, and visualization adapters to prioritize Java first. Support for other languages will be added incrementally using language-specific strategies.

## Alternatives Considered
- **Simultaneous Multi-Language Release**: Building execution sandboxes and parsers for Java, Python, and C++ concurrently. This was rejected because of excessive complexity and different runtime behaviors (e.g. Python garbage collection vs. C++ direct pointers).
- **Language-Agnostic Core**: Designing a generic model before implementing any language. This was rejected due to risk of premature abstractions that may fail to address the specific mechanics of JVM call stacks and object structures.

## Reasoning
Starting with Java targets a widely used language in CS education, allowing us to build, test, and validate JVM call-stack debugging mechanics.

## Consequences
- Shared memory trace contracts (`TimelineFrame`) will initially reflect JVM properties (e.g., stack frames, local variables, object heap addresses).
- Future languages (e.g. Python, C++) will implement adapter wrappers to conform to this trace format.
