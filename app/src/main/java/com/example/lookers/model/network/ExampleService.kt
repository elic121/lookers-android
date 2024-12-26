package com.example.lookers.model.network

import com.example.lookers.model.entity.example.ExampleEntity
import retrofit2.Call
import retrofit2.http.GET

interface ExampleService {
    @GET("/")
    fun getExampleData(): Call<ExampleEntity>
}