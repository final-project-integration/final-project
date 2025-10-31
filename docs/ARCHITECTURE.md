# System Architecture

## Overview

This project is a **monorepo Java console application** consisting of multiple independent modules that work together to provide a Personal Finance Manager (PFM).

Each module owns its responsibilities and code, and integration happens through a shared contract (method signatures + Java classes/objects).

---

## Folder Structure

```
final-project/
 ├── accounts/        # User authentication + credential flows
 ├── storage/         # Persistent data storage and retrieval
 ├── validation/      # Data validation before processing
 ├── reports/         # Financial summaries + display/export
 ├── prediction/      # Budget "What-If" calculations
 ├── test/            # QA, test scripts, bug tracking
 ├── integration/     # CLI menu + orchestration
 └── docs/            # All project documentation
```

**Rule:**
*Code lives in module folders. Documentation lives in `/docs`.*

---

## Module Responsibilities

| Module      | Core Responsibility                                 |
| ----------- | --------------------------------------------------- |
| Accounts    | Login, password reset, account CRUD                 |
| Storage     | Load/save user CSV data, persist budget objects     |
| Validation  | Validate CSV format, dates, numbers, categories     |
| Reports     | Generate monthly/yearly summary reports             |
| Prediction  | Run hypothetical budget scenarios (surplus/deficit) |
| Test        | Validate functionality, log bugs                    |
| Integration | Menu, program flow, enforce shared API              |

---

## Architecture Principles

###  Separation of Concerns

Each module has one job.

###  Contract-based design

Modules interact through **shared method signatures**, defined in:

```
docs/API_CONTRACTS.md
```

###  Stable interfaces

Public method contracts must not break other teams.

**Breaking changes require Integration approval.**

Additive changes allowed.

---

## Data Flow

```
User Input
    ↓
Accounts → Authentication Success?
    ↓ yes
Main Menu (Integration)
    ↓
──> Load CSV (Storage)
    ↓
Validation → Errors? → Ask user to fix / ignore rows
    ↓
Budget Data Object Lives in Storage
    ↓
Option 1: Reports → Display or Export CSV
Option 2: Prediction → Run scenarios
Option 3: Delete Data / Logout / Exit
```

---

## Data Model Summary

| Object      | Purpose                                      |
| ----------- | -------------------------------------------- |
| User        | Credentials + recovery question              |
| Transaction | Date, Category, Amount                       |
| BudgetData  | Collection of transactions + computed totals |

Full definitions: `docs/DATA_MODEL.md`

---

## Execution Flow

```
Start Program
↓
Accounts.login()
↓
Integration Main Menu
↓
User chooses module
↓
Run <module>.run()
↓
Return to menu until exit
```

---

## Integration Rules

* Integration owns CLI menu and system wiring
* Integration reviews API changes
* Modules expose a `run()` entry point
* If a module changes a shared method → update API doc + notify

---

## Build & Deployment

* CLI based application
* Final output: runnable JAR

---

## Design Goals

* Modular, team-friendly structure
* Replaceable components
* Minimal shared dependencies
* Consistent user experience across modules
* Fail-safe behavior (input should not crash system)

---

## Summary

This architecture ensures each team can build independently while maintaining a stable, functioning system through **clear contracts, integration entry points, and modular boundaries.**


