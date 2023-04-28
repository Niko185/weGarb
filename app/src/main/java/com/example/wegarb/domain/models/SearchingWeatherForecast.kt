package com.example.wegarb.domain.models

data class SearchingWeatherForecast (
    val currentData: String,
    val currentTemperature: Int,
    val currentWind: String,
    val currentCondition: String,
    val currentCityName: String,
    val currentCoordinate: String
    )