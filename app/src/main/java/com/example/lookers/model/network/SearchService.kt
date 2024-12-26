package com.example.lookers.model.network

import com.example.lookers.model.entity.search.SearchProdEntity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("search/el")
    fun searchProdByName(
        @Query("q") query: String
    ): Call<List<SearchProdEntity>>
}