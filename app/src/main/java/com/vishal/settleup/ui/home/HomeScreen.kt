package com.vishal.settleup.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vishal.settleup.ui.common.rememberCurrentUser
import com.vishal.settleup.utils.toCurrency

@Composable
fun HomeScreen() {
    val vm: HomeViewModel = viewModel()

    // ✅ Unified session access
    val user = rememberCurrentUser() ?: return

    val balances by vm.balances.collectAsState()
    val expenses by vm.expenses.collectAsState()
    val settlements by vm.settlements.collectAsState()

    var showAddExpense by remember { mutableStateOf(false) }

    fun displayName(userId: String): String {
        return if (userId == user.id) user.name else userId
    }

    // TEMP until groups: members derived from balances
    val members = remember(balances) {
        balances.keys.toList()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpense = true }) {
                Text("+")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔹 Balances
            Text(text = "Balances")
            Spacer(modifier = Modifier.height(8.dp))

            balances.forEach { (userId, balance) ->
                val color =
                    if (balance.netBalance >= 0) Color(0xFF2E7D32)
                    else Color(0xFFC62828)

                Text(
                    text = "${displayName(userId)} : ${balance.netBalance.toCurrency()}",
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Expenses
            Text(text = "Expenses")
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseRow(
                        expense = expense,
                        onDelete = { expenseId ->
                            vm.deleteExpense(expenseId)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Settlements
            Text(text = "Settle Up")
            settlements.forEach {
                Text(
                    text = "${displayName(it.fromUserId)} → ${displayName(it.toUserId)} ${it.amount.toCurrency()}"
                )
            }
        }
    }

    if (showAddExpense) {
        AddExpenseSheet(
            members = members,        // ✅ NEW
            onDismiss = { showAddExpense = false },
            onAddExpense = { expense ->
                vm.addExpense(expense)
                showAddExpense = false
            }
        )
    }
}
