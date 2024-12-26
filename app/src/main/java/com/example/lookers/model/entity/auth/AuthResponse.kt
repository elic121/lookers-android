package com.example.lookers.model.entity.auth

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("profileImage")
    val profileImage: String,
)
