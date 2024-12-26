package com.example.lookers.model.entity.auth

import com.google.gson.annotations.SerializedName

data class DeviceTokenRequest(
    @SerializedName("deviceToken")
    val deviceToken: String
)