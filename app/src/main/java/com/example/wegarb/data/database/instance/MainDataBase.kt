package com.example.wegarb.data.database.instance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.interfacedb.Dao
import com.example.wegarb.utils.DatabaseConvertor


@Database (entities = [InfoModel::class], version = 1)
@TypeConverters(DatabaseConvertor::class)
abstract class MainDataBase() : RoomDatabase() {

    abstract fun getDao(): Dao

    companion object {
        @Volatile
        var INSTANCE: MainDataBase? = null
        fun getDataBase(context: Context): MainDataBase {
            return INSTANCE ?:

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, MainDataBase::class.java, "weGarb.db").build()
                INSTANCE = instance
                return instance
            }
        }
    }

}