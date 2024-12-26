package com.example.lookers.model.entity.auth

import com.google.gson.annotations.SerializedName

data class DeviceTokenResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("nickname")
    val nickname: String?,

    @SerializedName("profileImage")
    val profileImage: String?,

    @SerializedName("deviceToken")
    val deviceToken: String
)