package com.example.wegarb.domain.models.main.search_request.weather_search_request

data class SysInfo(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Long,
    val sunset: Long,
)

