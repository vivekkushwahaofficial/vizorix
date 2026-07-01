# ADR 0001: Monorepo Organization

## Status
Approved

## Context
The Vizorix project consists of multiple modules, including frontend client interfaces, backend servers, shared model libraries, parsing subsystems, and secure code sandbox executors. Managing these across separate code repositories increases overhead for testing, package versioning, and continuous integration.

## Decision
We will organize the codebase as a modular monorepo structure. Deployable targets will reside under `apps/` and shared sub-system libraries will reside under `packages/`.

## Alternatives Considered
- **Multi-Repo Structure**: Splitting client, server, and parser packages into individual repositories. This was rejected because it introduces complexity for cross-package changes and increases package publishing overhead.
- **Traditional Monolith**: Grouping all features into a single backend framework (e.g. Spring Boot serving views). This was rejected due to lack of scalability and tight coupling between frontend visualizers and backend compilation runtimes.

## Reasoning
A monorepo structure offers a single source of truth, speeds up contract changes across client and server boundaries, and makes developer setup straightforward.

## Consequences
- Requires unified lockfiles.
- Enhances code reusability (especially sharing types between backend servers and frontend clients).
- Restricts build scopes so changes to a single package do not require rebuilding unaffected modules.
