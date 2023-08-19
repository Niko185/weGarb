package com.example.wegarb.domain.models.weather

data class SearchWeather(
    override val date: String,
    override val city: String,
    override val temperature: Int,
    override val description: String,
    override val windSpeed: String,
    override val latitude: String,
    override val longitude: String,
    override val feltTemperature: Int,
    override val windDirection: String,
    override val humidity: String,
    override val image: String
) : Weather(date, temperature, description, windSpeed, latitude, longitude, feltTemperature, windDirection, humidity, city, image)
