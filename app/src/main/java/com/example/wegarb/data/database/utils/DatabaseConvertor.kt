
package com.example.wegarb.data.database.utils

import androidx.room.TypeConverter
import com.example.wegarb.domain.models.old.WardrobeElement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConvertor {

    @TypeConverter
    fun fromGarbList(garbList: MutableList<WardrobeElement>): String {
        return Gson().toJson(garbList)
    }

    @TypeConverter
    fun inGarbList(garbString: String): MutableList<WardrobeElement> {
        val garbType = object : TypeToken<MutableList<WardrobeElement>>() {}.type
        return Gson().fromJson(garbString, garbType)
    }

}
