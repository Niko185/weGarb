package com.example.wegarb.data

data class SearchWeatherModel (
    val currentData: String,
    val currentTemperature: Int,
    val currentWind: String,
    val currentCondition: String,
    val currentCityName: String,
    val currentCoordinate: String
    )