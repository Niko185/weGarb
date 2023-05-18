package com.example.wegarb.data.weather.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchWindDto(
    @SerializedName("speed")
    val windSpeed: Double,

    @SerializedName("deg")
    val windDirection: Int

    )

