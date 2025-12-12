package com.vishal.settleup.data.firebase

import com.google.firebase.database.FirebaseDatabase

object FirebaseRefs {

    private val database = FirebaseDatabase.getInstance()

    val groupsRef = database.getReference("groups")
    val expensesRef = database.getReference("expenses")
    val balancesRef = database.getReference("balances")
}
