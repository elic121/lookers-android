package com.example.lookers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookers.model.entity.user.UserEntity
import com.example.lookers.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel
    @Inject
    constructor(
        private val dataStoreRepository: DataStoreRepository,
    ) : ViewModel() {
        private val _exampleData = MutableLiveData<Int>()
        val exampleData: LiveData<Int> = _exampleData

        private val _userInfo = MutableLiveData<UserEntity>()
        val userInfo: LiveData<UserEntity> = _userInfo

        private val _accessToken = MutableLiveData<String>()
        val accessToken: LiveData<String> = _accessToken

        private val _refreshToken = MutableLiveData<String>()
        val refreshToken: LiveData<String> = _refreshToken

        fun setExampleData(data: Int) {
            viewModelScope.launch {
                dataStoreRepository.setExampleData(data)
            }
        }

        fun getExampleData() {
            viewModelScope.launch {
                val data = dataStoreRepository.getExampleData()
                _exampleData.value = data.first()
            }
        }

        fun saveTokens(
            accessToken: String,
            refreshToken: String?,
        ) {
            viewModelScope.launch {
                dataStoreRepository.saveTokens(accessToken, refreshToken)
            }
        }

        fun getAccessToken() {
            viewModelScope.launch {
                val data = dataStoreRepository.getAccessToken()
                _accessToken.value = data.first()
            }
        }

        fun getRefreshToken() {
            viewModelScope.launch {
                val data = dataStoreRepository.getRefreshToken()
                _refreshToken.value = data.first()
            }
        }

        fun getUserInfo() {
            viewModelScope.launch {
                val data = dataStoreRepository.getUserInfo().first()
                Timber.tag("DataStoreViewModel").d("getUserInfo: $data")
                _userInfo.value = data
            }
        }
    }
