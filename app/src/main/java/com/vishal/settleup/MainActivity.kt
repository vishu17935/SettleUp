package com.vishal.settleup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.vishal.settleup.domain.session.CurrentUserManager
import com.vishal.settleup.domain.session.UserPreferences
import com.vishal.settleup.ui.home.HomeScreen
import com.vishal.settleup.ui.onboarding.OnboardingScreen
import com.vishal.settleup.ui.theme.SettleUpTheme
import com.vishal.settleup.ui.group.GroupEntryScreen
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SettleUpTheme {

                val context = LocalContext.current
                val prefs = remember { UserPreferences(context) }
                val scope = rememberCoroutineScope()

                var isLoaded by remember { mutableStateOf(false) }
                var isLoggedIn by remember { mutableStateOf(false) }
                var hasGroup by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    combine(
                        prefs.userIdFlow,
                        prefs.userNameFlow,
                        prefs.groupIdFlow
                    ) { id, name, groupId ->
                        Triple(id, name, groupId)
                    }.collect { (id, name, groupId) ->

                        if (id != null && name != null) {
                            CurrentUserManager.setUser(id, name)
                            isLoggedIn = true
                        }

                        if (groupId != null) {
                            CurrentUserManager.setGroup(groupId)
                            hasGroup = true
                        } else {
                            hasGroup = false
                        }

                        isLoaded = true
                    }
                }

                when {
                    !isLoaded -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    !isLoggedIn -> {
                        OnboardingScreen { userId, name ->
                            scope.launch {
                                prefs.saveUser(userId, name)
                                CurrentUserManager.setUser(userId, name)
                                isLoggedIn = true
                            }
                        }
                    }

                    !hasGroup -> {
                        GroupEntryScreen(
                            onGroupJoined = { groupId ->
                                scope.launch {
                                    prefs.saveGroupId(groupId)
                                    CurrentUserManager.setGroup(groupId)
                                    hasGroup = true
                                }
                            }
                        )
                    }

                    else -> {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
