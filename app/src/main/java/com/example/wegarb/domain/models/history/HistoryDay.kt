package com.example.wegarb.domain.models.history

import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.domain.models.cloth.element_kit.WardrobeElement

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

{
    fun mapToEntity(): HistoryDayEntity {
        return HistoryDayEntity(
            id = id,
            date = date,
            temperature = temperature,
            feltTemperature = feltTemperature,
            description = description,
            windSpeed = windSpeed,
            windDirection = windDirection,
            cityName = cityName,
            status = status,
            clothingList = clothingList
        )
    }

}