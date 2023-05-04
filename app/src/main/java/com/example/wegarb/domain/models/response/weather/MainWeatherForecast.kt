package com.example.wegarb.domain.models.response.weather

import com.google.gson.annotations.SerializedName

data class MainWeatherForecast(

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("current")
    val currentWeatherForecast: CurrentWeatherForecast,

    @SerializedName("weather")
    val descriptionWeatherForecast: DescriptionWeatherForecast,
)


//val humidity: Int