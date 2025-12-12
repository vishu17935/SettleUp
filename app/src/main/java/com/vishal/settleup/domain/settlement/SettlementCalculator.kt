package com.vishal.settleup.domain.settlement

import com.vishal.settleup.data.models.Balance
import com.vishal.settleup.data.models.Settlement
import kotlin.math.abs
import kotlin.math.min

object SettlementCalculator {

    fun calculateSettlements(
        balances: Map<String, Balance>
    ): List<Settlement> {

        val creditors = balances.values
            .filter { it.netBalance > 0 }
            .map { it.userId to it.netBalance }
            .toMutableList()

        val debtors = balances.values
            .filter { it.netBalance < 0 }
            .map { it.userId to abs(it.netBalance) }
            .toMutableList()

        val settlements = mutableListOf<Settlement>()

        var i = 0
        var j = 0

        while (i < debtors.size && j < creditors.size) {

            val (debtorId, debtAmount) = debtors[i]
            val (creditorId, creditAmount) = creditors[j]

            val settleAmount = min(debtAmount, creditAmount)

            settlements.add(
                Settlement(
                    fromUserId = debtorId,
                    toUserId = creditorId,
                    amount = settleAmount
                )
            )

            debtors[i] = debtorId to (debtAmount - settleAmount)
            creditors[j] = creditorId to (creditAmount - settleAmount)

            if (debtors[i].second == 0.0) i++
            if (creditors[j].second == 0.0) j++
        }

        return settlements
    }
}
