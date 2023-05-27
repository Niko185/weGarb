package com.example.wegarb.data


import com.example.wegarb.data.weather.remote.api.WeatherApi
import com.example.wegarb.domain.WeatherRepository
import com.example.wegarb.domain.models.weather.LocationCityName
import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.domain.models.weather.SearchWeather
import java.lang.Exception

class WeatherRepositoryImpl(private val weatherApi: WeatherApi): WeatherRepository {

    override suspend fun getLocationWeatherForecast(latitude: Double, longitude: Double): LocationWeather {
        return try {
            val responseDtoBody = weatherApi.getLocationWeatherForecast(latitude, longitude)
            responseDtoBody.mapToDomain()

        } catch (error: Exception) {
            throw error
        }
    }

    override suspend fun getLocationCityName(latitude: Double, longitude: Double): List<LocationCityName> {
        return try {
            val responseDto = weatherApi.getLocationCityName(latitude, longitude)
            responseDto.map {
                it.mapToDomain()
            }

        } catch (error: Exception) {
            throw error
        }
    }

    override suspend fun getSearchWeatherForecast(cityName: String): SearchWeather {
        return try {
            val responseDto = weatherApi.getSearchWeatherForecast(cityName)
            responseDto.mapToDomain()

        } catch (error: Exception) {
            throw error
        }
    }
}








