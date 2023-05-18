package com.example.wegarb.data.weather.remote.dto.location

import com.example.wegarb.domain.models.weather.LocationWeather
import com.google.gson.annotations.SerializedName
// Response Body class for Location
data class LocationWeatherDto(

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("current")
    val currentInfo: LocationCurrentDto,

    @SerializedName("weather")
    val descriptionInfo: LocationDescriptionDto

    )

    {
    fun mapToDomain(): LocationWeather {
        return LocationWeather(
            date = currentInfo.date.toString(),
            temperature = currentInfo.temperature.toInt(),
            description = "",//currentInfo.descriptionInformationList.get(0).description.orEmpty(),
            windSpeed = currentInfo.windSpeed.toString(),
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            feltTemperature = currentInfo.feltTemperature.toInt(),
            windDirection = currentInfo.windDirection.toString(),
            humidity = currentInfo.humidity.toString(),
            ctiy = null
        )
        }
    }




