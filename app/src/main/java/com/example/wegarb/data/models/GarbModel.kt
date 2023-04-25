package com.example.wegarb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GarbModel(
    @PrimaryKey
    val nameGarb: String,
    val imageGarb: Int
)
