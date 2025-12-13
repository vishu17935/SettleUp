package com.vishal.settleup.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vishal.settleup.data.firebase.FirebaseRefs
import com.vishal.settleup.data.models.Expense

class ExpenseRepository {

    fun observeExpenses(
        groupId: String,
        onSuccess: (List<Expense>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        FirebaseRefs.expensesRootRef.child(groupId)
            .addValueEventListener(object : ValueEventListener {
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

    fun addExpense(groupId: String, expense: Expense) {
        val id = FirebaseRefs.expensesRootRef.child(groupId).push().key ?: return
        FirebaseRefs.expensesRootRef.child(groupId).child(id).setValue(expense.copy(id = id))
    }

    fun softDeleteExpense(
        groupId: String,
        expenseId: String,
        deletedByUserId: String
    ) {
        val updates = mapOf(
            "isDeleted" to true,
            "deletedAt" to System.currentTimeMillis(),
            "deletedByUserId" to deletedByUserId
        )

        FirebaseRefs.expensesRootRef
            .child(groupId)
            .child(expenseId)
            .updateChildren(updates)
    }

}
