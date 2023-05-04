package com.example.wegarb.domain.models.response.weather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherForecast(

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

    val weather: List<DescriptionWeatherForecast>
)

