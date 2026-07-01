# System Architecture - Vizorix

## Purpose
This document details the high-level system design, service communications, subsystem decomposition, and data flow patterns.

---

## Table of Contents
1.  [Architectural Design Patterns](#1-architectural-design-patterns)
2.  [System Topology & Services](#2-system-topology--services)
3.  [Data Flow Diagram](#3-data-flow-diagram)
4.  [Subsystem Interfaces](#4-subsystem-interfaces)
5.  [Security & Sandbox Boundaries](#5-security--sandbox-boundaries)

---

## 1. Architectural Design Patterns
*Clean Architecture, Domain-Driven Design (DDD), and Microservice-based isolation of JVM runner processes.*

## 2. System Topology & Services
*Mapping client interface to Nginx router, API gateways, execution sandboxes, Redis trace stores, and LLM explanation services.*

## 3. Data Flow Diagram
*Visualizing compilation requests, execution traces, visual trace structures, and AI context assembly flow.*

## 4. Subsystem Interfaces
*Interface contracts for apps/web, apps/api, packages/execution-engine, packages/visualization-core, and packages/ai-core.*

## 5. Security & Sandbox Boundaries
*Isolation strategies for untrusted user code execution (Docker, gVisor, or firecracker).*
