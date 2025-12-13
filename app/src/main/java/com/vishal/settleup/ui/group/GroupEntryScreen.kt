package com.vishal.settleup.ui.group

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.vishal.settleup.data.repository.GroupRepository
import com.vishal.settleup.domain.session.CurrentUserManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupEntryScreen(
    onGroupJoined: (String) -> Unit
) {
    val user = CurrentUserManager.getUserOrNull() ?: return
    val repo = remember { GroupRepository() }

    var mode by remember { mutableStateOf(Mode.NONE) }
    var groupName by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SettleUp",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (mode) {
                Mode.NONE -> {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { mode = Mode.CREATE }
                    ) {
                        Text("Create a group")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { mode = Mode.JOIN }
                    ) {
                        Text("Join with code")
                    }
                }

                Mode.CREATE -> {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Group name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = groupName.isNotBlank() && !loading,
                        onClick = {
                            loading = true
                            error = null

                            repo.createGroup(
                                groupName = groupName.trim(),
                                userId = user.id,
                                userName = user.name,
                                onSuccess = { groupId ->
                                    onGroupJoined(groupId)
                                },
                                onError = {
                                    error = it.message
                                    loading = false
                                }
                            )
                        }
                    ) {
                        Text("Create group")
                    }
                }

                Mode.JOIN -> {
                    OutlinedTextField(
                        value = joinCode,
                        onValueChange = {
                            if (it.length <= 6) joinCode = it
                        },
                        label = { Text("6-digit code") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = joinCode.length == 6 && !loading,
                        onClick = {
                            loading = true
                            error = null

                            repo.joinGroupByCode(
                                code = joinCode,
                                userId = user.id,
                                userName = user.name,
                                onSuccess = { groupId ->
                                    onGroupJoined(groupId)
                                },
                                onError = {
                                    error = it.message
                                    loading = false
                                }
                            )
                        }
                    ) {
                        Text("Join group")
                    }
                }
            }

            if (loading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private enum class Mode {
    NONE, CREATE, JOIN
}
