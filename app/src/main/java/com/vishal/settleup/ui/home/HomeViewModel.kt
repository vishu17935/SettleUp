package com.vishal.settleup.ui.home

import androidx.lifecycle.ViewModel
import com.vishal.settleup.data.models.Balance
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.data.repository.ExpenseRepository
import com.vishal.settleup.domain.balance.BalanceCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    private val repository: ExpenseRepository = ExpenseRepository()
) : ViewModel() {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    val expenses: StateFlow<List<Expense>> = _expenses

    private val _balances = MutableStateFlow<Map<String, Balance>>(emptyMap())
    val balances: StateFlow<Map<String, Balance>> = _balances

    init {
        observeExpenses()
    }

    private fun observeExpenses() {
        repository.observeExpenses(
            onSuccess = { expenseList ->
                _expenses.value = expenseList
                _balances.value = BalanceCalculator.calculateBalances(expenseList)
            },
            onError = {
                // TODO: handle error state later
            }
        )
    }
}
