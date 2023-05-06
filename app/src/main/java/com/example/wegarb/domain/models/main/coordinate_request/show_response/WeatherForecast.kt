package com.example.wegarb.domain.models.main.coordinate_request.show_response

data class WeatherForecast(
    val date: String,
    val temperature: Int,
    val description: String,
    val windSpeed: String,
    val currentLatitude: String,
    val currentLongitude: String,
    val cityName: String,
    val feltTemperature: Int,
    val windDirection: String,
    val humidity: String
)
