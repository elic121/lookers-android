package com.example.lookers.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.lookers.model.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository
@Inject
constructor(
    private val dataStore: DataStore<Preferences>,
) {
    companion object {
        val EXAMPLE_KEY = intPreferencesKey("count_number")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PROFILE_IMAGE = stringPreferencesKey("user_profile_image")
    }

    suspend fun setExampleData(data: Int) {
        dataStore.edit { settings ->
            settings[EXAMPLE_KEY] = data
        }
    }

    fun getExampleData(): Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[EXAMPLE_KEY] ?: 0
        }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String? = null,
    ) {
        dataStore.edit { settings ->
            settings[ACCESS_TOKEN] = accessToken
            refreshToken?.let { settings[REFRESH_TOKEN] = refreshToken }
        }
    }

    fun getAccessToken(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }

    fun getRefreshToken(): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN] ?: ""
        }

    suspend fun saveUserInfo(
        id: Int?,
        name: String?,
        email: String?,
        profileImage: String?,
    ) {
        dataStore.edit { settings ->
            settings[USER_ID] = id ?: -1
            settings[USER_NAME] = name ?: ""
            settings[USER_EMAIL] = email ?: ""
            settings[USER_PROFILE_IMAGE] = profileImage ?: ""
        }
    }

    fun getUserInfo(): Flow<UserEntity> =
        dataStore.data.map { preferences ->
            UserEntity(
                preferences[USER_ID] ?: 0,
                preferences[USER_NAME] ?: "",
                preferences[USER_EMAIL] ?: "",
                preferences[USER_PROFILE_IMAGE] ?: "",
            )
        }

    suspend fun deleteAll() {
        dataStore.edit { settings ->
            settings.clear()
        }
    }
}
