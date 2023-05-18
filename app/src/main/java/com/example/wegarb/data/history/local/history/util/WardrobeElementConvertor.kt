
package com.example.wegarb.data.history.local.history.util

import androidx.room.TypeConverter
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WardrobeElementConvertor {

    @TypeConverter
    fun fromWardrobeElementList(list: List<WardrobeElement>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun inWardrobeElementList(wardrobeElement: String): List<WardrobeElement> {
        val wardrobeElementType = object : TypeToken<List<WardrobeElement>>() {}.type
        return Gson().fromJson(wardrobeElement, wardrobeElementType)
    }

}
