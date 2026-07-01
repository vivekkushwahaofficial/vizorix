# Security Policy

We take security and vulnerabilities seriously at Vizorix. This document outlines our policy, how to report vulnerabilities, and what to expect in response.

---

## Supported Versions

Only the current branch `main` and release tracks derived from it receive active security updates.

| Version | Supported |
| :--- | :--- |
| `0.1.x` (Prerelease) | Yes |

---

## Reporting a Vulnerability

**Do NOT open a public GitHub issue for security vulnerabilities.**

If you discover a security issue or vulnerability (e.g. sandbox breakout in execution-engine, data exposure, server vulnerability), please email **security@vizorix.com** with details.

Please include the following details in your report:
1.  **Vulnerability Type**: (e.g. Remote Code Execution, Privilege Escalation)
2.  **Impact**: Scope of the threat (e.g. Host-level access, user data exposure)
3.  **Proof of Concept (PoC)**: Step-by-step description of how to reproduce the issue, along with code snippets, API payloads, or scripts where applicable.
4.  **Sandbox Context**: If reporting a sandbox bypass, specify the Java versions and code patterns utilized.

We will acknowledge receipt within **24 hours** and provide updates regarding a fix or mitigation plan.

---

## Sandbox Security Scope

Given that Vizorix executes arbitrary user-supplied code (initially Java), sandbox integrity is critical. 
The following are treated as high-priority security vectors:
*   Bypassing runtime limits (CPU, memory, threads).
*   File system read/write operations outside of execution scopes.
*   Unauthorized network access.
*   Shared library execution (JNI/JNA calls).
*   Process spawning or host OS command injection.
