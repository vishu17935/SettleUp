package com.vishal.settleup.domain.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrentUserManager {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName

    fun setUser(id: String, name: String) {
        _userId.value = id
        _displayName.value = name
    }

    fun isLoggedIn(): Boolean {
        return _userId.value != null && _displayName.value != null
    }
    fun getUserOrNull(): SessionUser? {
        val id = userId.value
        val name = displayName.value
        return if (id != null && name != null) SessionUser(id, name) else null
    }

}
