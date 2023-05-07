package com.example.wegarb.data.weather.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchCoordinateDto(
    @SerializedName("lon")
    val longitude: Double,
    @SerializedName("lat")
    val latitude: Double,
)

