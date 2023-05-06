package com.example.wegarb.domain.models.main.coordinate_request.weather_request

import com.google.gson.annotations.SerializedName

data class DescriptionWeatherForecast (
    @SerializedName("main")
    val description: String
    )

