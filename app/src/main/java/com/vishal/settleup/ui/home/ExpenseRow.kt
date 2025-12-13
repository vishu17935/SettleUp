package com.vishal.settleup.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.vishal.settleup.data.models.Expense
import com.vishal.settleup.domain.session.CurrentUserManager
import com.vishal.settleup.utils.toCurrency
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseRow(
    expense: Expense,
    onDelete: (String) -> Unit
) {
    val currentUserId by CurrentUserManager.userId.collectAsState()
    val currentUserName by CurrentUserManager.displayName.collectAsState()

    if (currentUserId == null || currentUserName == null) return

    val canDelete =
        !expense.isDeleted && expense.createdByUserId == currentUserId

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (canDelete && value == SwipeToDismissBoxValue.EndToStart) {
                onDelete(expense.id)
                true
            } else {
                false
            }
        }
    )

    if (canDelete) {
        SwipeToDismissBox(
            modifier = Modifier.fillMaxWidth(),
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Red)
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }
            },
            content = {
                ExpenseContent(
                    expense = expense,
                    currentUserId = currentUserId!!,
                    currentUserName = currentUserName!!
                )
            }
        )
    } else {
        ExpenseContent(
            expense = expense,
            currentUserId = currentUserId!!,
            currentUserName = currentUserName!!
        )
    }
}

@Composable
private fun ExpenseContent(
    expense: Expense,
    currentUserId: String,
    currentUserName: String
) {
    fun displayName(userId: String?): String {
        if (userId == null) return "Unknown"
        return if (userId == currentUserId) currentUserName else userId
    }

    val fadedColor = if (expense.isDeleted) Color.Gray else Color.Unspecified
    val textDecoration =
        if (expense.isDeleted) TextDecoration.LineThrough else null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = expense.note.ifBlank { "Expense" },
            style = MaterialTheme.typography.titleMedium,
            color = fadedColor,
            textDecoration = textDecoration
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${expense.amount.toCurrency()} • Paid by ${displayName(expense.payerId)}",
            style = MaterialTheme.typography.bodyMedium,
            color = fadedColor,
            textDecoration = textDecoration
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Added by ${displayName(expense.createdByUserId)} · ${expense.createdAt.toTimeString()}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        if (expense.isDeleted) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Deleted by ${displayName(expense.deletedByUserId)} · ${expense.deletedAt?.toTimeString()}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

private fun Long.toTimeString(): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}
