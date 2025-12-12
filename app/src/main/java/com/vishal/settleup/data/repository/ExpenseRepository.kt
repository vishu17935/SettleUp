package com.vishal.settleup.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vishal.settleup.data.firebase.FirebaseRefs
import com.vishal.settleup.data.models.Expense
import kotlinx.coroutines.tasks.await

class ExpenseRepository {

    fun observeExpenses(
        onSuccess: (List<Expense>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        FirebaseRefs.expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val expenses = snapshot.children.mapNotNull {
                    it.getValue(Expense::class.java)
                }
                onSuccess(expenses)
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.toException())
            }
        })
    }

    // ✅ ADD THIS (required for Phase 3)
    suspend fun addExpense(expense: Expense) {
        val expenseId = FirebaseRefs.expensesRef.push().key
            ?: throw IllegalStateException("Failed to generate expense ID")

        val expenseWithId = expense.copy(id = expenseId)

        FirebaseRefs.expensesRef
            .child(expenseId)
            .setValue(expenseWithId)
            .await()
    }

    fun deleteExpense(expenseId: String) {
        FirebaseRefs.expensesRef
            .child(expenseId)
            .removeValue()
    }
}
