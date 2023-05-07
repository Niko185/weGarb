package com.example.wegarb

import android.app.Application
import com.example.wegarb.data.storage.AppDatabase

class HistoryDayApp : Application() {
    val appDatabaseInitialization by lazy { AppDatabase.getDataBase(this) }
}