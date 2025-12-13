package com.vishal.settleup.domain.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CurrentUserManager {

    // ─── User ────────────────────────────────────────────────
    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName

    // ─── Group ───────────────────────────────────────────────
    private val _groupId = MutableStateFlow<String?>(null)
    val groupId: StateFlow<String?> = _groupId

    // ─── Setters ─────────────────────────────────────────────
    fun setUser(id: String, name: String) {
        _userId.value = id
        _displayName.value = name
    }

    fun setGroup(groupId: String) {
        _groupId.value = groupId
    }

    // ─── Safe accessors (NON-reactive) ───────────────────────
    fun getUserOrNull(): SessionUser? {
        val id = _userId.value
        val name = _displayName.value
        return if (id != null && name != null) {
            SessionUser(id, name)
        } else null
    }

    fun getGroupIdOrNull(): String? {
        return _groupId.value
    }

    fun clear() {
        _userId.value = null
        _displayName.value = null
        _groupId.value = null
    }
}
