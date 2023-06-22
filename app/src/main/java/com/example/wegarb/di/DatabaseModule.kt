package com.example.wegarb.di

import android.content.Context
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.data.history.local.dao.HistoryDayDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideHistoryDayDao(database: AppDatabase): HistoryDayDao {
        return database.historyDayDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDataBase(context)
    }
}