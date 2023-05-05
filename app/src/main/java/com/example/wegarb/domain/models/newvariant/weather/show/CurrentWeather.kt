package com.example.wegarb.domain.models.newvariant.weather.show

data class CurrentWeather(
    val date: String,
    val temperature: Int,
    val description: String,
    val windSpeed: String,
    val currentLatitude: String,
    val currentLongitude: String,
    val cityName: String,
    val feltTemperature: String,
    val windDirection: String,
    val humidity: String
)
