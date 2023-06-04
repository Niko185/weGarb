package com.example.wegarb.data.history.local.entity

import androidx.room.*
import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement
import com.example.wegarb.data.history.local.util.WardrobeElementConvertor
import com.example.wegarb.domain.models.history.HistoryDay
import java.io.Serializable

@Entity(tableName = "history_day_table")
@TypeConverters(WardrobeElementConvertor::class)
data class HistoryDayEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "date_column")
    val date: String,

    @ColumnInfo(name = "temperature_column")
    val temperature: String,

    @ColumnInfo(name = "felt_temperature_colum")
    val feltTemperature: String,

    @ColumnInfo(name = "description_column")
    val description: String,

    @ColumnInfo(name = "wind_speed_column")
    val windSpeed: String,

    @ColumnInfo(name = "wind_direction_column")
    val windDirection: String,

    @ColumnInfo(name = "city_column")
    val cityName: String,

    @ColumnInfo(name = "status_column")
    val status: String,

    @ColumnInfo("wardrobe_element_list_column")
    val clothingList: List<WardrobeElement>


) : Serializable

{
    fun mapToDomain(): HistoryDay {
    return HistoryDay(
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



    companion object {
        fun mapFromDomain(historyDay: HistoryDay): HistoryDayEntity {
            return HistoryDayEntity(
                id = historyDay.id,
                date = historyDay.date,
                temperature = historyDay.temperature,
                feltTemperature = historyDay.feltTemperature,
                description = historyDay.description,
                windSpeed = historyDay.windSpeed,
                windDirection = historyDay.windDirection,
                cityName = historyDay.cityName,
                status = historyDay.status,
                clothingList = historyDay.clothingList
            )
        }
    }
}
