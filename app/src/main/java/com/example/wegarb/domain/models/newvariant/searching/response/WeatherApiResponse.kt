package com.example.wegarb.domain.models.newvariant.searching.response

data class WeatherApiResponse(
    val coord: Coordinates,
    val weather: List<Weather>,
    val base: String,
    val main: MainWeatherInfo,
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

