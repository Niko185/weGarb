package com.example.wegarb.data.weather.remote.dto.location

import com.google.gson.annotations.SerializedName

data class LocationCurrentDto(

    @SerializedName("dt")
    val date: Int,

    @SerializedName("temp")
    val temperature: Double,

    @SerializedName("wind_speed")
    val windSpeed: Double,

    @SerializedName("feels_like")
    val feltTemperature: Double,

    @SerializedName("wind_deg")
    val windDirection: Int,

    val humidity: Int,

    @SerializedName("weather")
    val descriptionInformationList: List<LocationDescriptionDto>
)

