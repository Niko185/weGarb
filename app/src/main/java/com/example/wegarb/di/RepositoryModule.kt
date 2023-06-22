package com.example.wegarb.di

import com.example.wegarb.data.repository.HistoryRepositoryImpl
import com.example.wegarb.data.repository.WeatherRepositoryImpl
import com.example.wegarb.domain.repository.HistoryRepository
import com.example.wegarb.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository {
        return weatherRepositoryImpl
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(historyRepositoryImpl: HistoryRepositoryImpl): HistoryRepository {
        return historyRepositoryImpl
    }
}