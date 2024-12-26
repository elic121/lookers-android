package com.example.lookers.model.network

import com.example.lookers.model.entity.embedded.WifiRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface EmbeddedService {
    @POST("wifi")
    suspend fun sendWifiInfo(@Body wifiRequest: WifiRequest): Response<Unit>
}