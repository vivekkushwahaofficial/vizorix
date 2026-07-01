# Vizorix

> Understand Code by Watching It Execute.

Vizorix is an AI-powered code execution visualization platform. It helps students, developers, and educators understand how Java programs execute internally through interactive, step-by-step visualizations of call stack frames, local variables, and heap objects, accompanied by contextual AI explanations.

---

## Vision

Computer science education often struggles with abstract concepts of memory management. Vizorix transforms source code from static text into a living, visual system. By stepping through execution cycles in real-time, learners can visualize exactly how values change, when memory is allocated on the heap, how frames push onto the stack, and why specific logical branches occur.

---

## Repository Structure

We organize this codebase as a modular monorepo:

```text
vizorix/
├── .github/                   # Issue templates, PR templates, and CODEOWNERS
├── apps/                      # Deployable applications
│   ├── api/                   # Backend gateway, routing and coordinator APIs (README only)
│   └── web/                   # User-facing client application (Editor & Visualizers - README only)
├── packages/                  # Reusable domain libraries
│   ├── ai/                    # LLM integration utilities and explanation logic (README only)
│   ├── execution-engine/      # Code execution executor and sandbox environment (README only)
│   ├── shared/                # Common interfaces, payloads, and domain objects (README only)
│   ├── ui/                    # Standardized design system component primitives (README only)
│   └── visualization/         # Trace analyzer mapping logs to state frames (README only)
├── docs/                      # Product specifications, system architecture, and ADRs
│   └── architecture-decisions/# Architecture Decision Records (ADRs)
├── design/                    # Layout designs, mockups, wireframes, and logos
├── examples/                  # Reserved for sample code payloads
└── assets/                    # Project brand assets, logos, and illustrations
```

---

## Documentation Links

### Root Documentation
*   [ROADMAP.md](ROADMAP.md): High-level project planning roadmap and milestones.
*   [PACKAGE-OVERVIEW.md](PACKAGE-OVERVIEW.md): Visual layout and descriptions of package dependencies.

### Technical & System Specifications
Structured specifications and architecture layout decisions reside under the [docs/](docs/) directory:
*   [01-PRD: Product Requirements Document](docs/01-PRD.md)
*   [02-SRS: Software Requirements Specification](docs/02-SRS.md)
*   [03-System-Architecture: High-level Blueprint](docs/03-System-Architecture.md)
*   [04-Execution-Engine: Compiler and Sandbox Design](docs/04-Execution-Engine.md)
*   [05-Roadmap: Product Development Milestones](docs/05-Roadmap.md)
*   [DECISIONS: Architecture Decisions Index](docs/DECISIONS.md)

---

## How to Clone

```bash
git clone https://github.com/vizorix/vizorix.git
cd vizorix
```

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
