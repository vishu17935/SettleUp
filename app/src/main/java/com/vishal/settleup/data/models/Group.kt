package com.vishal.settleup.data.models

data class Group(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
