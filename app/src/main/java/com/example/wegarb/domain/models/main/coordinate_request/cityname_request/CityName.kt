package com.example.wegarb.domain.models.main.coordinate_request.cityname_request

import com.google.gson.annotations.SerializedName

data class CityName(
   @SerializedName("local_names")
    val name: Map<String, String>,
)


