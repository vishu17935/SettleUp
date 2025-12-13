package com.vishal.settleup.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseSheet(
    members: List<String>,
    onDismiss: () -> Unit,
    onAddExpense: (Expense) -> Unit
) {
    val user = rememberCurrentUser() ?: return

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

    var payerId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(members) {
        if (payerId == null || payerId !in members) {
            payerId = when {
                user.id in members -> user.id
                members.isNotEmpty() -> members.first()
                else -> null
            }
        }
    }

    var selectedParticipants by remember {
        mutableStateOf(members.associateWith { true })
    }

    var payerExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Add Expense", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Payer (STABLE DROPDOWN)
            Text("Paid by")
            Spacer(modifier = Modifier.height(8.dp))

            Box {
                OutlinedTextField(
                    value = payerId?.let { displayName(it) } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payer") },
                    trailingIcon = {
                        IconButton(onClick = { payerExpanded = true }) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select payer"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
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

            // 🔹 Split between
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
                    val payer = payerId ?: return@Button
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
                        payerId = payer,
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
