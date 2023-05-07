package com.example.wegarb.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.wegarb.data.storage.local.history.dto.HistoryDayDto
import com.example.wegarb.data.storage.local.history.dao.HistoryDayDao
import com.example.wegarb.data.storage.local.history.util.HistoryDayWardrobeElementConvertor


@Database (entities = [HistoryDayDto::class], version = 1)
@TypeConverters(HistoryDayWardrobeElementConvertor::class)
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