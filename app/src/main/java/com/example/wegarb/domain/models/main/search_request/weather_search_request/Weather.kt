package com.example.wegarb.domain.models.main.search_request.weather_search_request

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)
