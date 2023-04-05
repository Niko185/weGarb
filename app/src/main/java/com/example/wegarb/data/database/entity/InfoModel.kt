package com.example.wegarb.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "info_entity")
data class InfoModel(

    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "date_column")
    val date: String,

    @ColumnInfo(name = "current_temp_column")
    val currentTemp: String

): Serializable

