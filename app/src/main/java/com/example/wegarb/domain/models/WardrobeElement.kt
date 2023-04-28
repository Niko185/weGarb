package com.example.wegarb.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WardrobeElement(
    @PrimaryKey
    val nameGarb: String,
    val imageGarb: Int
)
