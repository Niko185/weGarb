package com.example.wegarb.data.weather.remote.dto.location.city

import com.example.wegarb.domain.models.LocationCityNameInfo
import com.google.gson.annotations.SerializedName

data class LocationCityNameDto(
   @SerializedName("local_names")
   val name: Map<String, String>,
)

class LocationCityNameMapper() {

   fun mapToDomain(locationCityNameDto: LocationCityNameDto): LocationCityNameInfo {
      return LocationCityNameInfo(
         name = locationCityNameDto.name["en"] ?: "name city not found"
      )
   }
}


