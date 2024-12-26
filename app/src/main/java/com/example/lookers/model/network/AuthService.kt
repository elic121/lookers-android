package com.example.lookers.model.network

import com.example.lookers.model.entity.auth.AuthRequest
import com.example.lookers.model.entity.auth.AuthResponse
import com.example.lookers.model.entity.auth.DeviceTokenRequest
import com.example.lookers.model.entity.auth.DeviceTokenResponse
import com.example.lookers.model.entity.auth.TokenRequest
import com.example.lookers.model.entity.auth.TokenResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {
    @POST("auth/google")
    fun googleLogin(
        @Body request: AuthRequest
    ): Call<AuthResponse>

    @POST("auth/refresh")
    fun refreshAccessToken(
        @Body refreshToken: TokenRequest
    ): Call<TokenResponse>

    @PATCH("user")
    fun sendFirebaseToken(
        @Body deviceToken: DeviceTokenRequest
    ): Call<DeviceTokenResponse>
}