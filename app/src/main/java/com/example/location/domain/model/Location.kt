package com.example.location.domain.model

data class Location(
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val bearing: Float = 0f, // Góc xoay của thiết bị (0-360 độ)
    val timestamp: Long = System.currentTimeMillis(),
    val displayName: String = ""
)
