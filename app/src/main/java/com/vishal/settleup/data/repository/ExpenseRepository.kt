package com.vishal.settleup.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.vishal.settleup.data.firebase.FirebaseRefs
import com.vishal.settleup.data.models.Expense

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
}
