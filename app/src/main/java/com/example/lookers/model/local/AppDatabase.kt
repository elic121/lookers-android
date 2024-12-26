package com.example.lookers.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lookers.model.entity.example.ExampleEntity
import com.example.lookers.model.local.dao.ExampleDao

@Database(version = 1, exportSchema = false, entities = [ExampleEntity::class])

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exampleDao(): ExampleDao
}
