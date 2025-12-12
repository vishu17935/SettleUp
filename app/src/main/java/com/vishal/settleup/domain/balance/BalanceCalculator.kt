package com.vishal.settleup.domain.balance

import com.vishal.settleup.data.models.Balance
import com.vishal.settleup.data.models.Expense

object BalanceCalculator {

    fun calculateBalances(expenses: List<Expense>): Map<String, Balance> {

        val paidMap = mutableMapOf<String, Double>()
        val owedMap = mutableMapOf<String, Double>()

        for (expense in expenses) {

            // 1️⃣ Payer paid full amount
            paidMap[expense.payerId] =
                (paidMap[expense.payerId] ?: 0.0) + expense.amount

            // 2️⃣ Each participant owes their share
            for ((userId, participant) in expense.participants) {
                owedMap[userId] =
                    (owedMap[userId] ?: 0.0) + participant.shareAmount
            }
        }

        // 3️⃣ Merge into Balance objects
        val userIds = paidMap.keys + owedMap.keys

        return userIds.associateWith { userId ->
            val paid = paidMap[userId] ?: 0.0
            val owed = owedMap[userId] ?: 0.0

            Balance(
                userId = userId,
                totalPaid = paid,
                totalOwed = owed,
                netBalance = paid - owed
            )
        }
    }
}
