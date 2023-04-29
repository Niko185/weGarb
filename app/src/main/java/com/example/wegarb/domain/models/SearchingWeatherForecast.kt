package com.example.wegarb.domain.models

data class SearchingWeatherForecast (
    val date: String,
    val temperature: Double,
    val windSpeed: Double,
    val description: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
    )