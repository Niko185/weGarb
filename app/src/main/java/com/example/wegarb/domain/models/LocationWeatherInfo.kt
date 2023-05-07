package com.example.wegarb.domain.models

data class LocationWeatherInfo(
    val date: String,
    val temperature: Int,
    val description: String,
    val windSpeed: String,
    val latitude: String,
    val longitude: String,
    val feltTemperature: Int,
    val windDirection: String,
    val humidity: String
)
