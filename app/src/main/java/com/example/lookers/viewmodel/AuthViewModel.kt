package com.example.lookers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookers.model.entity.ResultState
import com.example.lookers.model.entity.auth.AuthResponse
import com.example.lookers.model.entity.auth.DeviceTokenResponse
import com.example.lookers.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authResponse = MutableLiveData<ResultState<AuthResponse>>()
    val authResponse: LiveData<ResultState<AuthResponse>> = _authResponse

    private val _deviceTokenResponse = MutableLiveData<ResultState<DeviceTokenResponse>>()
    val deviceTokenResponse: LiveData<ResultState<DeviceTokenResponse>> = _deviceTokenResponse

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            _authResponse.value = ResultState.Loading

            val result = authRepository.googleLogin(idToken)
            _authResponse.value = if (result.isSuccess) {
                ResultState.Success(result.getOrNull()!!)
            } else {
                ResultState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    suspend fun sendFirebaseToken(deviceToken: String) {
        viewModelScope.launch {
            val result = authRepository.sendFirebaseToken(deviceToken)
            _deviceTokenResponse.value = if (result.isSuccess) {
                ResultState.Success(result.getOrNull()!!)
            } else {
                ResultState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}
