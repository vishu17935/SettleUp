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
import androidx.compose.foundation.lazy.LazyRow
import com.vishal.settleup.ui.home.SettlementChip
import com.vishal.settleup.ui.home.GroupHeader




@Composable
fun HomeScreen() {
    val vm: HomeViewModel = viewModel()

    // ✅ Unified session access
    val user = rememberCurrentUser() ?: return

    val balances by vm.balances.collectAsState()
    val expenses by vm.expenses.collectAsState()
    val settlements by vm.settlements.collectAsState()
    val groupMembers by vm.groupMembers.collectAsState() // ✅ NEW
    val group by vm.group.collectAsState()

    var showAddExpense by remember { mutableStateOf(false) }

    fun displayName(userId: String): String {
        return groupMembers[userId] ?: userId
    }

    val members = remember(groupMembers) {
        groupMembers.keys.toList()
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
            group?.let {
                GroupHeader(
                    groupName = it.name,
                    joinCode = it.joinCode
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 🔹 Balances
            Text(text = "Balances")
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                balances.forEach { (userId, balance) ->
                    item {
                        BalanceCard(
                            name = displayName(userId),
                            amount = balance.netBalance
                        )
                    }
                }
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
            // 🔹 Settlements
            Text(text = "Settle Up")
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                settlements.forEach { settlement ->
                    item {
                        SettlementChip(
                            fromName = displayName(settlement.fromUserId),
                            toName = displayName(settlement.toUserId),
                            amount = settlement.amount
                        )
                    }
                }
            }

        }
    }

    if (showAddExpense) {
        AddExpenseSheet(
            members = members,               // ✅ REAL group members
            onDismiss = { showAddExpense = false },
            onAddExpense = { expense ->
                vm.addExpense(expense)
                showAddExpense = false
            }
        )
    }
}
