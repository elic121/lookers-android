package com.example.lookers.di

import android.content.Context
import androidx.room.Room
import com.example.lookers.model.local.AppDatabase
import com.example.lookers.model.local.dao.ExampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    fun provideExampleDao(database: AppDatabase): ExampleDao {
        return database.exampleDao()
    }
}