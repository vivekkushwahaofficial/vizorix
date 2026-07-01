# ADR 0002: Clean Architecture & DDD Patterns

## Status
Approved

## Context
The core business logic of Vizorix involves parsing execution trace events and converting them into memory visual states (stacks, heaps, objects). Coupling this logic directly to database drivers, API routing frameworks, or frontend UI layout views would limit code reusability and complicate unit testing.

## Decision
We will employ Clean Architecture layers and Domain-Driven Design (DDD) boundaries. Core memory models, execution traces parser engines, and evaluation rules are kept completely decoupled from web servers, database connectors, and browser render frameworks.

## Alternatives Considered
- **Framework-Integrated Services**: Implementing trace translation directly in Spring Boot services or React state wrappers. This was rejected because it binds educational core rules to specific runtime libraries and frameworks.
- **Anemic Domain Models**: Treating shared structures as pure property sets without behavior. This was rejected in favor of rich DDD models representing state transitions step-by-step.

## Reasoning
This layout makes the tracing and parsing rules testable without mock HTTP requests, web pages, or database instances, while keeping the core domain clean.

## Consequences
- Involves writing interface adapter layers to map domain classes to client payloads.
- Enables changing backend or frontend frameworks (e.g. from Node to Spring Boot, or React to another web framework) without rewrite of trace validation packages.
