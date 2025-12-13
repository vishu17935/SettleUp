package com.vishal.settleup.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.settleup.data.models.Balance
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.data.models.Settlement
import com.vishal.settleup.data.repository.ExpenseRepository
import com.vishal.settleup.domain.balance.BalanceCalculator
import com.vishal.settleup.domain.settlement.SettlementCalculator
import com.vishal.settleup.domain.session.CurrentUserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _balances = MutableStateFlow<Map<String, Balance>>(emptyMap())
    val balances: StateFlow<Map<String, Balance>> = _balances

    private val _settlements = MutableStateFlow<List<Settlement>>(emptyList())
    val settlements: StateFlow<List<Settlement>> = _settlements

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        repository.observeExpenses(
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
        repository.deleteExpense(expenseId)
    }

    fun addExpense(expense: Expense) {
        // ✅ SINGLE source of truth
        val user = CurrentUserManager.getUserOrNull() ?: return

        val enrichedExpense = expense.copy(
            createdByUserId = user.id,
            createdAt = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.addExpense(enrichedExpense)
        }
    }
}
