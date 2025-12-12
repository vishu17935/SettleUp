# Phase 1 Report — SettleUp (Core Ledger & Balances)

## Overview

This document captures **everything implemented in Phase 1** of the *SettleUp* Android application. It serves as a **freeze-point reference** and **rollback guide** before starting Phase 2 (UI expansion, ledger list, settlement logic, etc.).

Phase 1 focuses on **correctness, architecture, and data flow**, not UI polish.

---

## Phase 1 Objectives (Achieved)

* Firebase Realtime Database setup and verification
* Clean MVVM architecture
* Firebase-safe data models
* Correct expense aggregation logic
* Accurate per-user balance computation
* Live data → ViewModel → UI pipeline
* GitHub version control with clean checkpoint

---

## Project Structure (Phase 1)

```
app/src/main/java/com/vishal/settleup
│
├── data
│   ├── firebase
│   │   ├── FirebaseManager.kt
│   │   └── FirebaseRefs.kt
│   │
│   ├── models
│   │   ├── Expense.kt
│   │   ├── Participant.kt
│   │   ├── SplitType.kt
│   │   ├── Group.kt
│   │   └── Balance.kt
│   │
│   └── repository
│       └── ExpenseRepository.kt
│
├── domain
│   └── balance
│       └── BalanceCalculator.kt
│
├── ui
│   ├── home
│   │   ├── HomeScreen.kt
│   │   └── HomeViewModel.kt
│   │
│   └── theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
└── MainActivity.kt
```

---

## File-by-File Breakdown

### 1. MainActivity.kt

**Role:** Entry point of the app

* Hosts Compose UI
* Delegates all logic to `HomeViewModel`
* Contains no Firebase or business logic

Purpose:

* Ensures Activity remains "dumb"
* Survives configuration changes safely

---

### 2. data/models/

These are **Firebase-safe, immutable models** with default values for deserialization.

#### Expense.kt

* Represents a single expense entry
* Source of truth for all computations

Key fields:

* `amount`
* `payerId`
* `participants` (map for O(1) lookup)
* `splitType`

---

#### Participant.kt

* Represents a user's share in an expense
* Encodes split results (not split rules)

---

#### SplitType.kt

* Enum defining supported split mechanisms
* Used for UI and future validation

---

#### Group.kt

* Represents logical grouping of expenses
* Phase 1 assumes a single group

---

#### Balance.kt

* **Derived model** (not stored as source of truth)
* Used for UI rendering

Fields:

* `totalPaid`
* `totalOwed`
* `netBalance`

---

### 3. data/firebase/

#### FirebaseManager.kt

* Centralized Firebase initialization
* Ensures singleton database instance

---

#### FirebaseRefs.kt

* Central place for database paths
* Avoids string duplication

Paths defined:

* `/groups`
* `/expenses`
* `/balances`

---

### 4. data/repository/

#### ExpenseRepository.kt

**Role:** Firebase data access layer

Responsibilities:

* Writing expenses
* Observing expenses (Realtime listener)
* Converting Firebase snapshots → models

Design decisions:

* Listeners live in repository
* ViewModel consumes clean data

---

### 5. domain/balance/

#### BalanceCalculator.kt

**Most critical file in Phase 1**

Responsibilities:

* Aggregate expenses
* Compute per-user balances
* Pure function (no side effects)

Algorithm:

1. Payer gets +amount
2. Participants owe shareAmount
3. Net balance = paid − owed

Properties:

* Deterministic
* Testable
* Independent of Firebase & UI

---

### 6. ui/home/

#### HomeViewModel.kt

Responsibilities:

* Observes expenses from repository
* Computes balances via `BalanceCalculator`
* Exposes state via `StateFlow`

Why StateFlow:

* Lifecycle-safe
* Compose-friendly
* Easy future migration to Room cache

---

#### HomeScreen.kt

Responsibilities:

* Displays live balances
* Subscribes to ViewModel state

Phase 1 UI philosophy:

* Minimal UI
* Focus on correctness
* No premature styling

---

## Firebase Setup (Phase 1)

### Firebase Product Used

* **Realtime Database**

### Database Mode

* Test mode (for development)

### Verified Operations

* Write expense
* Read expenses
* Live updates

---

## Gradle & Build Configuration Changes

### Version Catalog (`libs.versions.toml`)

**Added versions:**

* Google Services Plugin
* Firebase Realtime Database

**Added libraries:**

* `firebase-database-ktx`

**Added plugins:**

* `com.google.gms.google-services`

---

### Project-level `build.gradle.kts`

* Added Google Services plugin with `apply false`
* No legacy `buildscript` usage

---

### App-level `build.gradle.kts`

**Plugins added:**

* Google Services plugin

**Dependencies added:**

* Firebase Realtime Database (KTX)

**Existing stack retained:**

* Jetpack Compose
* Kotlin DSL
* Material 3

---

## Minimum SDK & Tooling

* **Min SDK:** 24 (Android 7.0)
* **Target SDK:** 34
* **Compile SDK:** 34
* **Java/Kotlin target:** JVM 11

---

## Git & Version Control

* GitHub repository created: `SettleUp`
* Remote `origin` configured
* Phase 1 committed and pushed

Recommended rollback strategy:

```bash
git checkout <phase-1-commit-hash>
```

---

## What Phase 1 Does NOT Include (By Design)

* Expense list UI
* Settlement algorithm
* Edit/delete expenses
* Group switching UI
* Charts or analytics
* Authentication
* Offline persistence

These belong to Phase 2 and beyond.

---

## Phase 1 Success Criteria (All Met)

* Correct balances
* Live updates
* Clean architecture
* Firebase safety
* No Activity-level business logic
* Git checkpoint available

---

## Conclusion

Phase 1 establishes a **rock-solid foundation** for SettleUp. The app is already functionally correct at a core level and ready for Phase 2 enhancements without architectural risk.

This document should be preserved as the **Phase 1 rollback reference**.

---

**Status:** Phase 1 locked and complete ✅
