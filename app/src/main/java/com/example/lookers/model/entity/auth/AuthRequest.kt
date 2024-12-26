package com.example.lookers.model.entity.auth

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("id_token")
    val idToken: String
)
