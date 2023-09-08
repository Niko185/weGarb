package com.example.wegarb.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wegarb.data.history.local.dao.HistoryDayDao
import com.example.wegarb.data.history.local.entity.HistoryDayEntity
import com.example.wegarb.data.history.local.util.WardrobeElementConvertor


@Database (entities = [HistoryDayEntity::class], version = 1, exportSchema = true)
@TypeConverters(WardrobeElementConvertor::class)

abstract class AppDatabase() : RoomDatabase() {

    abstract fun historyDayDao(): HistoryDayDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null
        fun getDataBase(context: Context): AppDatabase {
            return INSTANCE ?:

            synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "weGarb.db").build()
                INSTANCE = instance
                return instance
            }
        }
    }
}