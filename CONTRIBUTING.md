# Contributing to Vizorix

Welcome! We are excited that you want to contribute to Vizorix. This document provides developers with instructions, coding standards, and processes to contribute code to the repository.

---

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Folder Responsibilities](#folder-responsibilities)
3. [Branch Strategy](#branch-strategy)
4. [Commit Conventions](#commit-conventions)
5. [Pull Request Workflow](#pull-request-workflow)
6. [Coding Standards](#coding-standards)
7. [Dependency Management](#dependency-management)
8. [Versioning](#versioning)
9. [Code Review Process](#code-review-process)

---

## Code of Conduct
All contributors are expected to follow our [Code of Conduct](CODE_OF_CONDUCT.md) in all community and code interactions.

---

## Folder Responsibilities
We structure Vizorix as a modular monorepo. Every folder has a clear boundary:

*   **`apps/`**: Deployable applications.
    *   `web/`: User-facing web application (Editor & Visualizer interface - README only).
    *   `api/`: Backend Gateway & Orchestrator (README only).
*   **`packages/`**: Shared libraries and core modules.
    *   `execution-engine/`: Sandbox runner for compile and execution steps (README only).
    *   `shared/`: Contract payloads and type bindings (README only).
    *   `visualization/`: Engine traces parser translating logs to memory states (README only).
    *   `ai/`: Prompt assembly and LLM translation helpers (README only).
    *   `ui/`: Design system CSS variables and presentation elements (README only).
*   **`infrastructure/`**: Centralized routing, telemetry, and deploy files (will contain deployment assets beginning in Milestone 3).
*   **`docs/`**: Specifications, roadmap, and ADRs.
*   **`design/`**: Mockups, layouts, and wireframes repository.
*   **`examples/`**: Code payloads and examples index.

---

## Branch Strategy
We follow a structured branching system based on GitFlow conventions:

| Branch Name | Source Branch | Target Branch | Purpose |
| :--- | :--- | :--- | :--- |
| `main` | - | - | Production-ready state. |
| `develop` | `main` | `main` | Primary integration branch. All active development targets this branch. |
| `feature/*` | `develop` | `develop` | Adding new features. Formatted as `feature/issue-number-short-desc`. |
| `bugfix/*` | `develop` | `develop` | Fixing bugs in current development cycle. |
| `hotfix/*` | `main` | `main` & `develop` | Direct fixes for production-critical bugs. |
| `release/*` | `develop` | `main` & `develop` | Release candidate testing and preparation. |

### Creating a Branch
```bash
# Example for a new feature branch
git checkout develop
git pull origin develop
git checkout -b feature/101-java-parser
```

---

## Commit Conventions
We enforce the **Conventional Commits** standard (validated via commitlint).

### Format
`type(scope): description`

### Types
*   `feat`: A new feature (corresponds to MINOR in SemVer).
*   `fix`: A bug fix (corresponds to PATCH in SemVer).
*   `docs`: Documentation changes.
*   `style`: Formatting, missing semi-colons (no code changes).
*   `refactor`: Code refactoring that does not change functional behavior.
*   `perf`: Performance improvement.
*   `test`: Adding or correcting tests.
*   `chore`: Tooling, script, or workspace configuration updates.
*   `build`: Changes affecting dependencies or compile systems.

### Scopes
The scope must specify the impacted module or application, such as `web`, `api`, `execution-engine`, `shared`, `visualization`, `ai`, or `ui`.

### Example
```text
feat(execution-engine): support compilation of nested classes
```

---

## Pull Request Workflow
1.  **Sync Local**: Always update your local `develop` branch before branch creation.
2.  **Verify Quality**: Verify that files compile cleanly.
3.  **Submit PR**: Create a Pull Request against the `develop` branch.
4.  **Fill Template**: Fill out the [Pull Request Template](.github/pull_request_template.md).
5.  **Code Review**: Obtain approvals from code owners before merging.

---

## Coding Standards

### Naming Conventions
*   **Directories/Folders**: Use kebab-case (e.g. `execution-engine`).
*   **Files**: Kebab-case for source and config files (e.g. `ast-parser.ts`, `.eslintrc.json`).
*   **Types/Classes/Interfaces**: PascalCase (e.g. `JavaParser`, `IVariableState`).
*   **Functions/Variables**: camelCase (e.g. `compileCode()`, `stackPointer`).
*   **Constants**: UPPER_SNAKE_CASE (e.g. `MAX_TIMEOUT_MS`).

### General Practices
*   Write self-documenting code. Code should be clean enough that comments explain *why*, not *what*.
*   Strict TypeScript typing should be enforced. Avoid using `any` wherever possible.
*   Maximize test coverage. Every new class or domain model requires a unit test.

---

## Dependency Management
*   **Lockfiles**: Do not manually modify lockfiles. Always run the appropriate package manager commands (e.g., `npm install`, `pnpm install`, or `yarn install`) to update them.
*   **Scoping**: Add dependencies to the package that actually uses it. Do not bloat root configs with localized project dependencies.

---

## Versioning
This repository uses **Semantic Versioning (SemVer)**:
*   **Major Version (`X.y.z`)**: Incompatible API changes.
*   **Minor Version (`x.Y.z`)**: Added functionality in a backwards-compatible manner.
*   **Patch Version (`x.y.Z`)**: Backwards-compatible bug fixes.

Releases are tagged automatically based on conventional commit type.

---

## Code Review Process
*   **Reviewers**: Code is audited by at least one maintainer or specific CODEOWNERS.
*   **Requirements**:
    *   Build compiles cleanly without errors.
    *   No TypeScript warnings or ESLint failures.
    *   Adequate unit tests added.
    *   Passes manual inspection on architecture constraints (Clean Architecture layers, DDD domain logic isolates).

