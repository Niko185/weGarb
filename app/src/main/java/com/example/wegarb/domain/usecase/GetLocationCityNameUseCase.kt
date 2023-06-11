package com.example.wegarb.domain.usecase

import com.example.wegarb.domain.models.weather.LocationCityName
import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.domain.repository.WeatherRepository
import javax.inject.Inject

class GetLocationCityNameUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): List<LocationCityName> {
        return weatherRepository.getLocationCityName(latitude, longitude)
    }
}