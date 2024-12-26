package com.example.lookers.util

import androidx.lifecycle.MutableLiveData
import com.example.lookers.model.entity.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> makeRequest(
    request: suspend () -> retrofit2.Response<T>
): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = request()

            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

fun <T> handleResult(
    liveData: MutableLiveData<ResultState<T>>,
    result: Result<T>
) {
    liveData.value = if (result.isSuccess) {
        ResultState.Success(result.getOrNull()!!)
    } else {
        ResultState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
    }
}

fun <T> handleState(
    state: ResultState<T>,
    onLoading: () -> Unit,
    onSuccess: (data: T) -> Unit,
    onError: (message: String) -> Unit,
) {
    when (state) {
        is ResultState.Loading -> onLoading()
        is ResultState.Success -> onSuccess(state.data)
        is ResultState.Error -> onError(state.message)
    }
}
