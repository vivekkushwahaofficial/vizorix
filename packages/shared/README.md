# Shared Models Package (`packages/shared`)

## Purpose
Acts as the central contracts library for structures, events, and validation schemas used across apps and packages.

## Responsibility
- Declares TS types, models, schemas, and event structures.
- Standardizes request/response payloads (e.g., run requests, timeline outputs).
- Provides input validation schemas.

## Planned Public Interfaces
- `TraceEvent` (Interface for variable, stack, or heap events)
- `TimelineFrame` (Structure representing system memory at a step)
- `validateRunPayload(data: unknown): RunPayload`

## Dependencies
- None
