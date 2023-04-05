package com.example.wegarb.data.database.initialization

import android.app.Application
import com.example.wegarb.data.database.instance.MainDataBase

class MainDataBaseInitialization : Application() {
    val mainDataBaseInitialization by lazy { MainDataBase.getDataBase(this) }
}