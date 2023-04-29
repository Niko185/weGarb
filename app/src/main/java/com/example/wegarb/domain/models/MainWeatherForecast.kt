package com.example.wegarb.domain.models

data class MainWeatherForecast(
    val date: String,
    val temperature: Double,
    val windSpeed: Double,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

