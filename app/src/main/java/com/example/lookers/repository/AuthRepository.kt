package com.example.lookers.repository

import com.example.lookers.model.entity.auth.AuthRequest
import com.example.lookers.model.entity.auth.AuthResponse
import com.example.lookers.model.entity.auth.DeviceTokenRequest
import com.example.lookers.model.entity.auth.DeviceTokenResponse
import com.example.lookers.model.entity.auth.TokenRequest
import com.example.lookers.model.entity.auth.TokenResponse
import com.example.lookers.model.network.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend fun googleLogin(idToken: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.googleLogin(AuthRequest(idToken)).execute()

                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        dataStoreRepository.saveTokens(
                            authResponse.accessToken,
                            authResponse.refreshToken
                        )
                        dataStoreRepository.saveUserInfo(
                            authResponse.id,
                            authResponse.name,
                            authResponse.email,
                            authResponse.profileImage
                        )
                        Result.success(authResponse)
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun refreshAccessToken(refreshToken: String): Result<TokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authService.refreshAccessToken(TokenRequest(refreshToken)).execute()

                if (response.isSuccessful) {
                    response.body()?.let { token ->
                        dataStoreRepository.saveTokens(token.accessToken, token.refreshToken)
                        Result.success(TokenResponse(token.accessToken, token.refreshToken))
                    } ?: Result.failure(Exception("Empty response body"))
                } else {
                    Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun sendFirebaseToken(deviceToken: String?): Result<DeviceTokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                if (!deviceToken.isNullOrEmpty()) {
                    val response =
                        authService.sendFirebaseToken(DeviceTokenRequest(deviceToken)).execute()

                    if (response.isSuccessful) {
                        response.body()?.let { tokenResponse ->
                            dataStoreRepository.saveUserInfo(
                                tokenResponse.id,
                                tokenResponse.name,
                                tokenResponse.email,
                                tokenResponse.profileImage
                            )
                            Result.success(tokenResponse)
                        } ?: Result.failure(Exception("Empty response body"))
                    } else {
                        Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
                    }
                } else {
                    Result.failure(Exception("Device token is null or empty"))
                }
            } catch (e: Exception) {
                Timber.e(e)
                Result.failure(e)
            }
        }
    }
}
