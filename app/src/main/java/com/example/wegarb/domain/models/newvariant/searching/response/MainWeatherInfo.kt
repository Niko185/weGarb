package com.example.wegarb.domain.models.newvariant.searching.response

data class MainWeatherInfo(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
)

