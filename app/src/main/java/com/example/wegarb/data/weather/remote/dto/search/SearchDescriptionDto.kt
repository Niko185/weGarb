package com.example.wegarb.data.weather.remote.dto.search

import com.google.gson.annotations.SerializedName

data class SearchDescriptionDto(

    @SerializedName("main")
    val description: String

    )
