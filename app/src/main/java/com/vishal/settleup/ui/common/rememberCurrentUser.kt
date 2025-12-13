package com.vishal.settleup.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.vishal.settleup.domain.session.CurrentUserManager
import com.vishal.settleup.domain.session.SessionUser

@Composable
fun rememberCurrentUser(): SessionUser? {
    val id by CurrentUserManager.userId.collectAsState()
    val name by CurrentUserManager.displayName.collectAsState()
    return if (id != null && name != null) SessionUser(id!!, name!!) else null
}
