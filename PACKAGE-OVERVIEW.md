# Vizorix Monorepo Package Overview

This document summarizes the responsibilities, dependencies, and interfaces of all internal library modules under the `packages/` directory.

---

## Package Summary

| Package | Purpose | Core Interfaces | Dependencies |
| :--- | :--- | :--- | :--- |
| [`execution-engine`](packages/execution-engine) | Compiles and executes code in secure sandboxes. | `compile()`, `execute()` | None |
| [`visualization`](packages/visualization) | Translates raw trace logs to memory timeline frames. | `parseTraceStream()`, `getFrame()` | None |
| [`ai`](packages/ai) | Connects to LLMs to generate step-by-step comments. | `generateStepExplanation()` | None |
| [`shared`](packages/shared) | Shared types, schema validations, and DTO contracts. | `TraceEvent`, `TimelineFrame` | None |
| [`ui`](packages/ui) | Design system, layout components, and widgets. | `CodeEditorView`, `MemoryVisualizerView` | None |

---

## Component Integration Flow

The diagram below outlines the logical data flow between apps and library packages during a code run:

```text
  [apps/web] (UI Editor) 
      │ 
      │ (1) Code Submit
      ▼ 
  [apps/api] (Orchestrator)
      │ 
      ├─(2) Compile & Execute ──► [packages/execution-engine] (Sandbox)
      │                                       │
      │                                       ▼ Emits raw logs
      ├─(3) Translate Logs ◄──────────────────┘
      │      │
      │      ▼
      │   [packages/visualization] (Compiles TimelineFrame data)
      │ 
      ├─(4) Explanation Context ──► [packages/ai] (Generates comments)
      │                                    │
      │                                    ▼ Returns explanations
      ├─(5) Return timeline frames ◄───────┘
      │
      ▼
  [apps/web] (Renders Memory Graphics using [packages/ui] components)
```
