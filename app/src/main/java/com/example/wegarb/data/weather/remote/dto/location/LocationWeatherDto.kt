package com.example.wegarb.data.weather.remote.dto.location

import android.location.Location
import android.media.MicrophoneInfo.Coordinate3F
import com.example.wegarb.data.weather.remote.dto.location.LocationCurrentDto
import com.example.wegarb.data.weather.remote.dto.location.LocationDescriptionDto
import com.example.wegarb.domain.models.LocationWeatherInfo
import com.google.gson.annotations.SerializedName
// Response Body class for Location
data class LocationWeatherDto(

    @SerializedName("lat")
    val latitude: Double,

    @SerializedName("lon")
    val longitude: Double,

    @SerializedName("current")
    val currentInformation: LocationCurrentDto,

    @SerializedName("weather")
    val descriptionInformation: LocationDescriptionDto
)
class LocationWeatherMapper() {
    fun mapToDomain(locationWeatherDto: LocationWeatherDto): LocationWeatherInfo {
        val latitude = locationWeatherDto.latitude
        val longitude = locationWeatherDto.longitude
        val currentInformation = locationWeatherDto.currentInformation


        return LocationWeatherInfo(
            date = currentInformation.date.toString(),
            temperature = currentInformation.temperature.toInt(),
            description = currentInformation.descriptionInformationList.getOrNull(0)?.feeling.orEmpty(),
            windSpeed = currentInformation.windSpeed.toString(),
            latitude = latitude.toString(),
            longitude = longitude.toString(),
            feltTemperature = currentInformation.feltTemperature.toInt(),
            windDirection = currentInformation.windDirection.toString(),
            humidity = currentInformation.humidity.toString()
        )
    }
}


