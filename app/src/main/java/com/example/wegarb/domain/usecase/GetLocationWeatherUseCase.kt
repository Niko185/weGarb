package com.example.wegarb.domain.usecase

import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.domain.repository.WeatherRepository
import javax.inject.Inject

class GetLocationWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): LocationWeather {
        return weatherRepository.getLocationWeatherForecast(latitude, longitude)
    }
}