package com.example.wegarb.domain.usecase

import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.repository.WeatherRepository
import javax.inject.Inject

class GetSearchWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(city: String): SearchWeather {
       return weatherRepository.getSearchWeatherForecast(city)
    }
}


