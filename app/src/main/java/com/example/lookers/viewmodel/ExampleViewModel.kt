package com.example.lookers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookers.model.entity.example.ExampleEntity
import com.example.lookers.model.entity.ResultState
import com.example.lookers.repository.ExampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleRepository: ExampleRepository
): ViewModel() {
    private val _exampleEntity = MutableLiveData<ResultState<ExampleEntity>>()
    val exampleEntity: LiveData<ResultState<ExampleEntity>> = _exampleEntity

    fun getExampleData() {
        viewModelScope.launch {
            _exampleEntity.value = ResultState.Loading

            val result = exampleRepository.getExampleData()
            _exampleEntity.value = if (result.isSuccess) {
                ResultState.Success(result.getOrNull()!!)
            } else {
                ResultState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}