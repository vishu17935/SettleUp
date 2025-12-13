package com.vishal.settleup

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.os.Bundle
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

                LaunchedEffect(Unit) {
                    combine(
                        prefs.userIdFlow,
                        prefs.userNameFlow
                    ) { id, name ->
                        id to name
                    }.collect { (id, name) ->
                        if (id != null && name != null) {
                            CurrentUserManager.setUser(id, name)
                            isLoggedIn = true
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
                                isLoggedIn = true   // 🔥 immediate transition
                            }
                        }
                    }

                    else -> {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
