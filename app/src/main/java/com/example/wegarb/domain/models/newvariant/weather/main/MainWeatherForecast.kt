package com.example.wegarb.domain.models.newvariant.weather.main

import com.example.wegarb.domain.models.newvariant.weather.main.CurrentWeatherForecast
import com.example.wegarb.domain.models.newvariant.weather.main.DescriptionWeatherForecast
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


