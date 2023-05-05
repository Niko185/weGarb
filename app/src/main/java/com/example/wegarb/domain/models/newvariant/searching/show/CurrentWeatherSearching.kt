package com.example.wegarb.domain.models.newvariant.searching.show

data class CurrentWeatherSearching(
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
