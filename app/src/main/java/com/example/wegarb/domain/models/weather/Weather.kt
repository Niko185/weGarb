package com.example.wegarb.domain.models.weather

import com.example.wegarb.domain.models.cloth.BaseClothesKit


sealed class Weather(
   open val date: String,
   open val temperature: Int,
   open val description: String,
   open val windSpeed: String,
   open val latitude: String,
   open val longitude: String,
   open val feltTemperature: Int,
   open val windDirection: String,
   open val humidity: String,
   open val city: String
)

