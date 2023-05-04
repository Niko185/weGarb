package com.example.wegarb.domain.models.response.cityname

import com.google.gson.annotations.SerializedName

data class CityName(
   @SerializedName("local_names")
    val name: Map<String, String>,
)


