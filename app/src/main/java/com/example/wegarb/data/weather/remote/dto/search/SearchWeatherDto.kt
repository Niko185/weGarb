package com.example.wegarb.data.weather.remote.dto.search

import com.example.wegarb.domain.models.weather.SearchWeather
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

    )

    {
    fun mapToDomain(): SearchWeather {
        return SearchWeather(
            date = date.toString(),
            temperature = temperatureInfo.temperature.toInt() - 273,
            city = cityName,
            description = descriptionInfo.getOrNull(0)?.description.orEmpty(),
            windSpeed = windInfo.windSpeed.toString(),
            windDirection = windInfo.windDirection.toString(),
            latitude = coordinateInfo.latitude.toString(),
            longitude = coordinateInfo.longitude.toString(),
            feltTemperature = temperatureInfo.feltTemperature.toInt() - 273,
            humidity = temperatureInfo.humidity.toString(),
            image = descriptionInfo.getOrNull(0)?.image.orEmpty()
        )
        }
    }
