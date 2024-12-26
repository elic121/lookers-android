package com.example.lookers.repository

import com.example.lookers.model.entity.search.SearchProdEntity
import com.example.lookers.model.network.SearchService
import com.example.lookers.util.makeRequest
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchService: SearchService
) {
    suspend fun searchProdByName(query: String): Result<List<SearchProdEntity>> {
        return makeRequest { searchService.searchProdByName(query).execute() }
    }
}
