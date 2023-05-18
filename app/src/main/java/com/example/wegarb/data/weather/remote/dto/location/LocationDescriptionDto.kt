package com.example.wegarb.data.weather.remote.dto.location

import com.google.gson.annotations.SerializedName

data class LocationDescriptionDto (
    @SerializedName("main")
    val description: String
    )

