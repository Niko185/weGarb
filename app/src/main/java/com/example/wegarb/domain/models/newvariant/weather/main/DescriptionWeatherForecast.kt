package com.example.wegarb.domain.models.newvariant.weather.main

import com.google.gson.annotations.SerializedName

data class DescriptionWeatherForecast (
    @SerializedName("main")
    val description: String
    )

