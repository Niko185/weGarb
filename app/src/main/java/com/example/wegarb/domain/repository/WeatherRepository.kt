package com.example.wegarb.domain.repository

import com.example.wegarb.domain.models.weather.LocationCityName
import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.domain.models.weather.SearchWeather

interface WeatherRepository {
    suspend fun getLocationWeatherForecast(latitude: Double, longitude: Double): LocationWeather

    suspend fun getLocationCityName(lat: Double, lon: Double): List<LocationCityName>

    suspend fun getSearchWeatherForecast(cityName: String): SearchWeather
}