package com.example.location.domain.model

data class Room(
    val id: String = "",
    val name: String = "",
    val code: String = "",
    val creatorId: String = "",
    val members: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)
