package com.vishal.settleup.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.data.models.Participant
import com.vishal.settleup.data.models.SplitType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onAddExpense: (Expense) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var payerId by remember { mutableStateOf("user1") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(text = "Add Expense", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: return@Button

                    val expense = Expense(
                        amount = amt,
                        payerId = payerId,
                        splitType = SplitType.EQUAL,
                        note = note,
                        participants = mapOf(
                            "user1" to Participant("user1", amt / 3, 0.0, 0),
                            "user2" to Participant("user2", amt / 3, 0.0, 0),
                            "user3" to Participant("user3", amt / 3, 0.0, 0)
                        )
                    )

                    onAddExpense(expense)
                }
            ) {
                Text("Add")
            }
        }
    }
}
