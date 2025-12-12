package com.vishal.settleup.data.models

data class Participant(
    val userId: String = "",
    val shareAmount: Double = 0.0,
    val sharePercentage: Double = 0.0,
    val shares: Int = 0
)
