package com.vishal.settleup.data.models

data class Balance(
    val userId: String = "",
    val totalPaid: Double = 0.0,
    val totalOwed: Double = 0.0,
    val netBalance: Double = 0.0
)
