package com.example.wegarb.domain.models.history


import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement

data class HistoryDay (
    val id: Int?,
    val date: String,
    val temperature: String,
    val feltTemperature: String,
    val description: String,
    val windSpeed: String,
    val windDirection: String,
    val cityName: String,
    val status: String,
    val clothingList: List<WardrobeElement>
    )
