package com.vishal.settleup.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.data.models.Participant
import com.vishal.settleup.data.models.SplitType
import com.vishal.settleup.ui.common.rememberCurrentUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    members: List<String>,          // 👈 group members injected
    onDismiss: () -> Unit,
    onAddExpense: (Expense) -> Unit
) {
    val user = rememberCurrentUser() ?: return

    // 🔒 No members = no expense
    if (members.isEmpty()) {
        ModalBottomSheet(onDismissRequest = onDismiss) {
            Text(
                text = "No group members available",
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    fun displayName(userId: String): String =
        if (userId == user.id) user.name else userId

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    var payerId by remember { mutableStateOf(user.id) }

    var selectedParticipants by remember {
        mutableStateOf(members.associateWith { true })
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Add Expense", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Paid by")
            Spacer(modifier = Modifier.height(8.dp))

            var payerExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = payerExpanded,
                onExpandedChange = { payerExpanded = !payerExpanded }
            ) {
                OutlinedTextField(
                    value = displayName(payerId),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payer") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = payerExpanded)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = payerExpanded,
                    onDismissRequest = { payerExpanded = false }
                ) {
                    members.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(displayName(m)) },
                            onClick = {
                                payerId = m
                                payerExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Split between")
            Spacer(modifier = Modifier.height(8.dp))

            members.forEach { m ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selectedParticipants[m] == true,
                        onCheckedChange = { checked ->
                            selectedParticipants =
                                selectedParticipants.toMutableMap().apply {
                                    this[m] = checked
                                }
                        }
                    )
                    Text(displayName(m))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: return@Button
                    val participants = selectedParticipants.filterValues { it }.keys
                    if (amt <= 0 || participants.isEmpty()) return@Button

                    val splitAmount = amt / participants.size

                    val participantMap = participants.associateWith {
                        Participant(
                            userId = it,
                            shareAmount = splitAmount,
                            sharePercentage = 0.0,
                            shares = 0
                        )
                    }

                    val expense = Expense(
                        amount = amt,
                        payerId = payerId,
                        note = note,
                        splitType = SplitType.EQUAL,
                        participants = participantMap
                    )

                    onAddExpense(expense)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Expense")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
