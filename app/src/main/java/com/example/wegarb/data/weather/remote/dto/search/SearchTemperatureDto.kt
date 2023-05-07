package com.example.wegarb.data.weather.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchTemperatureDto(
    @SerializedName("temp")
    val temperature: Double,

    @SerializedName("feels_like")
    val feltTemperature: Double,

    val humidity: Int
)

