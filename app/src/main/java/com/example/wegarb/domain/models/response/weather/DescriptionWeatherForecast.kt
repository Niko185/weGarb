package com.example.wegarb.domain.models.response.weather

import com.google.gson.annotations.SerializedName

data class DescriptionWeatherForecast (
    @SerializedName("main")
    val description: String
    )

