package com.example.lookers.model.entity.auth

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
