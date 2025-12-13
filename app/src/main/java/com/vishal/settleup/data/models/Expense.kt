package com.vishal.settleup.data.models

data class Expense(
    val id: String = "",
    val groupId: String = "",
    val amount: Double = 0.0,
    val payerId: String = "",
    val participants: Map<String, Participant> = emptyMap(),
    val splitType: SplitType = SplitType.EQUAL,
    val note: String = "",

    // creation metadata
    val createdByUserId: String = "",
    val createdAt: Long = 0L,

    // 🔥 Soft delete fields (NEW)
    val isDeleted: Boolean = false,
    val deletedAt: Long? = null,
    val deletedByUserId: String? = null
)
