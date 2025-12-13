package com.vishal.settleup.data.models

data class Group(
    val id: String = "",
    val name: String = "",
    val joinCode: String = "",          // ✅ add this
    val members: Map<String, String> = emptyMap(), // userId -> displayName
    val createdAt: Long = 0L
)
