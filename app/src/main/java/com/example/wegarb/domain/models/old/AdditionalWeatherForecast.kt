package com.example.wegarb.domain.models.old

data class AdditionalWeatherForecast(
    val currentTemperature: Double,
    val feltTemperature: Double,
    val wind: Double,
    val windDirection: WindDirection,
    val humidity: Int
)
