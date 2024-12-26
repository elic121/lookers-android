package com.example.lookers.model.entity.embedded

data class WifiRequest(
    val level: Int,
    val ssid: String,
    val password: String,
    val userEmail: String,
)
