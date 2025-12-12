# Phase 2 Report — SettleUp (Ledger, Settlements & UX Polish)

## Overview

This document captures **all work completed in Phase 2** of the *SettleUp* Android application. Phase 2 builds on the solid data and balance foundation from Phase 1 and focuses on **usability, actionability, and interaction**, while preserving architectural correctness.

Phase 2 transforms the app from a balance calculator into a **fully usable expense-splitting product**.

---

## Phase 2 Objectives (Achieved)

* Display full expense ledger
* Compute and display settlement instructions
* Improve UI readability and clarity
* Enable expense deletion with live recomputation
* Maintain clean MVVM architecture
* Preserve Firebase correctness and reactivity

---

## Project Structure (Phase 2 Additions & Updates)

```
app/src/main/java/com/vishal/settleup
│
├── data
│   └── models
│       └── Settlement.kt
│
├── domain
│   └── settlement
│       └── SettlementCalculator.kt
│
├── ui
│   └── home
│       ├── ExpenseRow.kt   (updated)
│       ├── HomeScreen.kt  (updated)
│       └── HomeViewModel.kt (updated)
│
├── utils
│   └── CurrencyUtils.kt
```

---

## File-by-File Breakdown

### 1. Settlement.kt

**Role:** Represents a single settlement transaction

Fields:

* `fromUserId` (debtor)
* `toUserId` (creditor)
* `amount`

Used exclusively for UI and settlement presentation.

---

### 2. SettlementCalculator.kt

**Role:** Core settlement algorithm implementation

Responsibilities:

* Convert balances into actionable transactions
* Minimize number of payments

Algorithm:

1. Separate creditors and debtors
2. Match largest debtor with largest creditor
3. Settle minimum of the two
4. Repeat until balances are zeroed

Properties:

* Greedy, optimal solution
* Deterministic
* Pure function
* Independent of Firebase and UI

---

### 3. HomeViewModel.kt (Updated)

New responsibilities added in Phase 2:

* Compute settlements from balances
* Expose settlements as `StateFlow`
* Trigger settlement recomputation on expense changes

Design principles preserved:

* ViewModel remains orchestration-only
* All business logic delegated to domain layer

---

### 4. HomeScreen.kt (Updated)

Enhancements:

* Expense ledger list (`LazyColumn`)
* Settlement instruction section
* Clear visual separation of sections

UI Philosophy:

* Minimalistic
* Text-first
* No premature visual complexity

---

### 5. ExpenseRow.kt (Updated)

Enhancements:

* Swipe-to-delete support using Material 3
* Delete action wired to ViewModel

Key design decisions:

* Use `SwipeToDismissBox` (Material 3 API)
* Avoid Material 2 API mixing
* Deletion propagates instantly via Firebase

---

### 6. CurrencyUtils.kt

Purpose:

* Centralized currency formatting
* Improves UI clarity

Current behavior:

* Integer-only display (`₹100`)
* Rounding strategy deferred to later phase

---

## UI / UX Improvements (Phase 2)

### Balances

* Color-coded:

  * Green → user should receive money
  * Red → user owes money
* Currency symbol applied

### Expense Ledger

* Compact rows
* Payer and amount clearly visible
* Live updates on Firebase changes

### Settlements

* Arrow-based representation (`A → B ₹X`)
* Minimal but actionable

---

## Firebase Interaction (Phase 2)

### Operations Verified

* Read expenses
* Delete expense
* Live recomputation of balances
* Live recomputation of settlements

### Consistency Guarantee

* Expenses remain source of truth
* Balances and settlements are always derived

---

## Risky Features Implemented (Safely)

| Feature              | Risk   | Mitigation                    |
| -------------------- | ------ | ----------------------------- |
| Swipe-to-delete      | Medium | Firebase handles propagation  |
| Live recomputation   | Medium | Pure domain logic             |
| Settlement algorithm | Medium | Deterministic greedy approach |

---

## What Phase 2 Does NOT Include (By Design)

* Add Expense UI
* Edit expense
* Undo delete
* Charts / analytics
* Widgets
* Notifications
* Authentication

These are explicitly deferred to Phase 3.

---

## Git & Version Control

* Phase 2 committed as a separate checkpoint
* Clean rollback possible via Git

Recommended rollback:

```bash
git checkout <phase-2-commit-hash>
```

---

## Phase 2 Success Criteria (All Met)

* Expense ledger visible
* Settlements correct
* Live updates working
* Delete works correctly
* UI readable and understandable
* No architectural regressions

---

## Conclusion

Phase 2 completes the **core product experience** of SettleUp. The app now provides full visibility, understanding, and actionability for group expenses, while maintaining architectural integrity.

This document serves as the **Phase 2 rollback and reference point** before entering Phase 3.

---

**Status:** Phase 2 locked and complete ✅
