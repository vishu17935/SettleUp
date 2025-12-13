package com.vishal.settleup.data.firebase

import com.google.firebase.database.FirebaseDatabase

object FirebaseRefs {
    private val db = FirebaseDatabase.getInstance()

    val usersRef = db.getReference("users")
    val groupsRef = db.getReference("groups")

    // NOTE: expenses are now stored under expenses/{groupId}/{expenseId}
    val expensesRootRef = db.getReference("expenses")
}
