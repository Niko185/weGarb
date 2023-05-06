package com.example.wegarb.domain.models.main.search_request.weather_search_request

data class SearchWeatherForecast(
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,
    val main: Forecast,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Int,
    val sys: SysInfo,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int,
)

