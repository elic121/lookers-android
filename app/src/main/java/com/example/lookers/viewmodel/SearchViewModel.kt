package com.example.lookers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookers.model.entity.ResultState
import com.example.lookers.model.entity.search.SearchProdEntity
import com.example.lookers.repository.SearchRepository
import com.example.lookers.util.handleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _searchState = MutableLiveData<ResultState<List<SearchProdEntity>>>()
    val searchState: LiveData<ResultState<List<SearchProdEntity>>> = _searchState

    fun searchProdByName(query: String) {
        viewModelScope.launch {
            _searchState.value = ResultState.Loading
            val result = searchRepository.searchProdByName(query)
            handleResult(_searchState, result)
        }
    }
}
