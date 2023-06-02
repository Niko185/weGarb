package com.example.wegarb

import android.app.Application
import com.example.wegarb.data.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    val database by lazy { AppDatabase.getDataBase(this) }
}