package com.vishal.settleup.data.models

data class Expense(
    val id: String = "",
    val groupId: String = "",
    val amount: Double = 0.0,
    val payerId: String = "",
    val participants: Map<String, Participant> = emptyMap(),
    val splitType: SplitType = SplitType.EQUAL,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val createdByUserId: String = "",
    val createdAt: Long = 0L
)
