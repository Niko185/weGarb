package com.example.wegarb.data.weather.remote.dto.location.city

import com.example.wegarb.domain.models.weather.LocationCityName
import com.google.gson.annotations.SerializedName
// Response Body class for Location only City
data class LocationCityNameDto(

   @SerializedName("local_names")
   val cityName: Map<String, String>

   )

   {
   fun mapToDomain(): LocationCityName {
      return LocationCityName(
         name = cityName["en"] ?: "name city not found"
      )
      }
   }