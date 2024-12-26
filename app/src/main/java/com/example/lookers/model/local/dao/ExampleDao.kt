package com.example.lookers.model.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lookers.model.entity.example.ExampleEntity

@Dao
interface ExampleDao {
    @Query("SELECT * FROM example")
    suspend fun getExamples(): List<ExampleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exampleEntity: ExampleEntity)
}