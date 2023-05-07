package com.example.wegarb.data.weather.remote.dto.search

import com.example.wegarb.domain.models.SearchWeatherInfo
import com.google.gson.annotations.SerializedName

// Response Body class for Search
data class SearchWeatherDto(

    @SerializedName("dt")
    val date: Int,

    @SerializedName("name")
    val cityName: String,

    @SerializedName("coord")
    val coordinateInfo: SearchCoordinateDto,

    @SerializedName("weather")
    val descriptionInfo: List<SearchDescriptionDto>,

    @SerializedName("main")
    val temperatureInfo: SearchTemperatureDto,

    @SerializedName("wind")
    val windInfo: SearchWindDto

) class SearchWeatherMapper(){
    fun mapToDomain(searchWeatherDto: SearchWeatherDto): SearchWeatherInfo {
        return SearchWeatherInfo(
            date = searchWeatherDto.date.toString(),
            temperature = searchWeatherDto.temperatureInfo.temperature.toInt() - 273,
            cityName = searchWeatherDto.cityName.orEmpty(),
            description = searchWeatherDto.descriptionInfo.getOrNull(0)?.feeling.orEmpty(),
            windSpeed = searchWeatherDto.windInfo.windSpeed.toString(),
            windDirection = searchWeatherDto.windInfo.windDirection.toString(),
            currentLatitude = searchWeatherDto.coordinateInfo.latitude.toString(),
            currentLongitude = searchWeatherDto.coordinateInfo.longitude.toString(),
            feltTemperature = searchWeatherDto.temperatureInfo.feltTemperature.toInt() - 273,
            humidity = searchWeatherDto.temperatureInfo.humidity.toString()
        )
    }
}

