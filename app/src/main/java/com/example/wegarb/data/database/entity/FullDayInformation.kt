package com.example.wegarb.data.database.entity

import androidx.room.*
import com.example.wegarb.domain.models.WardrobeElement
import com.example.wegarb.utils.DatabaseConvertor
import java.io.Serializable

@Entity(tableName = "info_entity")
@TypeConverters(DatabaseConvertor::class)
data class FullDayInformation(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "date_column")
    val date: String,

    @ColumnInfo(name = "current_temp_column")
    val currentTemp: String,

    @ColumnInfo(name = "feels_like_colum")
    val currentFeelsLike: String,

    @ColumnInfo(name = "current_condition_column")
    val currentCondition: String,

    @ColumnInfo(name = "current_wind_column")
    val currentWind: String,

    @ColumnInfo(name = "wind_direction_column")
    val windDirection: String,

    @ColumnInfo(name = "current_city_column")
    val currentCity: String,

    @ColumnInfo(name = "status_forecast")
    val status: String,

    @ColumnInfo(name = "humidity_column")
    val humidity: String,

   @ColumnInfo("garb_column")
    val garb: MutableList<WardrobeElement>


) : Serializable

