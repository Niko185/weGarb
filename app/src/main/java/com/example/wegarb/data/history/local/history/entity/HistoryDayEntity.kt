package com.example.wegarb.data.history.local.history.entity

import androidx.room.*
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.example.wegarb.data.history.local.history.util.WardrobeElementConvertor
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

    @ColumnInfo(name = "humidity_column")
    val humidity: String,

    @ColumnInfo("wardrobe_element_list_column")
    val wardrobeElementList: List<WardrobeElement>


) : Serializable

