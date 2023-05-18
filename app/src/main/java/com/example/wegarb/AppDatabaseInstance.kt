package com.example.wegarb

import android.app.Application
import com.example.wegarb.data.AppDatabase

class AppDatabaseInstance : Application() {
    val database by lazy { AppDatabase.getDataBase(this) }
}