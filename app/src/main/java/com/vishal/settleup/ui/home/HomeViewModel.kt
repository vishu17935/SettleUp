package com.vishal.settleup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.settleup.data.models.Balance
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.data.models.Settlement
import com.vishal.settleup.data.repository.ExpenseRepository
import com.vishal.settleup.data.repository.GroupRepository // 🆕
import com.vishal.settleup.domain.balance.BalanceCalculator
import com.vishal.settleup.domain.settlement.SettlementCalculator
import com.vishal.settleup.domain.session.CurrentUserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val expenseRepository: ExpenseRepository = ExpenseRepository(),
    private val groupRepository: GroupRepository = GroupRepository() // 🆕
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _balances = MutableStateFlow<Map<String, Balance>>(emptyMap())
    val balances: StateFlow<Map<String, Balance>> = _balances

    private val _settlements = MutableStateFlow<List<Settlement>>(emptyList())
    val settlements: StateFlow<List<Settlement>> = _settlements

    // 🆕 Group members (userId -> displayName)
    private val _groupMembers = MutableStateFlow<Map<String, String>>(emptyMap())
    val groupMembers: StateFlow<Map<String, String>> = _groupMembers

    // ✅ resolve groupId ONCE
    private val groupId: String? = CurrentUserManager.getGroupIdOrNull()

    init {
        if (groupId != null) {
            observeGroup(groupId)      // 🆕
            observeExpenses(groupId)
        }
    }

    // 🆕 Observe group info
    private fun observeGroup(groupId: String) {
        groupRepository.observeGroup(
            groupId = groupId,
            onSuccess = { group ->
                _groupMembers.value = group.members
            },
            onError = {
                // TODO: handle later
            }
        )
    }

    private fun observeExpenses(groupId: String) {
        expenseRepository.observeExpenses(
            groupId = groupId,
            onSuccess = { expenseList ->
                _expenses.value = expenseList

                val balanceMap = BalanceCalculator.calculateBalances(expenseList)
                _balances.value = balanceMap

                _settlements.value =
                    SettlementCalculator.calculateSettlements(balanceMap)
            },
            onError = {
                // TODO: error handling later
            }
        )
    }

    fun deleteExpense(expenseId: String) {
        val gid = groupId ?: return
        expenseRepository.deleteExpense(gid, expenseId)
    }

    fun addExpense(expense: Expense) {
        val gid = groupId ?: return
        val user = CurrentUserManager.getUserOrNull() ?: return

        val enrichedExpense = expense.copy(
            createdByUserId = user.id,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            expenseRepository.addExpense(gid, enrichedExpense)
        }
    }
}
