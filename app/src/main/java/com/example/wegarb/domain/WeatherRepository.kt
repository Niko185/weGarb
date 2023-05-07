package com.example.wegarb.domain

import com.example.wegarb.domain.models.LocationCityNameInfo
import com.example.wegarb.domain.models.LocationWeatherInfo
import com.example.wegarb.domain.models.SearchWeatherInfo

interface WeatherRepository {
    suspend fun getLocationWeatherForecast(latitude: Double, longitude: Double): Result<LocationWeatherInfo>

    suspend fun getLocationCityName(lat: Double, lon: Double): Result<List<LocationCityNameInfo>>

    suspend fun getSearchWeatherForecast(cityName: String): Result<SearchWeatherInfo>
}