package com.example.wegarb.domain.models.main.search_request.weather_search_request

data class Forecast(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
)

