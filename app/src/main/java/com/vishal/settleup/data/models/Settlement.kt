package com.vishal.settleup.data.models

data class Settlement(
    val fromUserId: String,
    val toUserId: String,
    val amount: Double
)
