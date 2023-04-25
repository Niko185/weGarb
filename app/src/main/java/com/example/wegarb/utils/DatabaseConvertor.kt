
package com.example.wegarb.utils

import androidx.room.TypeConverter
import com.example.wegarb.data.models.GarbModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConvertor {

    @TypeConverter
    fun fromGarbList(garbList: MutableList<GarbModel>): String {
        return Gson().toJson(garbList)
    }

    @TypeConverter
    fun inGarbList(garbString: String): MutableList<GarbModel> {
        val garbType = object : TypeToken<MutableList<GarbModel>>() {}.type
        return Gson().fromJson(garbString, garbType)
    }

}
