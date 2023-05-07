package com.example.wegarb.domain.models

data class SearchWeatherInfo(
    val date: String,
    val cityName: String,
    val temperature: Int,
    val description: String,
    val windSpeed: String,
    val currentLatitude: String,
    val currentLongitude: String,
    val feltTemperature: Int,
    val windDirection: String,
    val humidity: String
)
