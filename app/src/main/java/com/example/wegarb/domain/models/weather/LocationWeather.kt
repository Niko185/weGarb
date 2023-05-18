package com.example.wegarb.domain.models.weather

data class LocationWeather(
    val date: String,
    val temperature: Int,
    val description: String,
    val windSpeed: String,
    val latitude: String,
    val longitude: String,
    val feltTemperature: Int,
    val windDirection: String,
    val humidity: String,
    val ctiy: LocationCityName?
)
