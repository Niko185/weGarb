package com.example.wegarb.data.weather.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchCoordinateDto(

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double

)

