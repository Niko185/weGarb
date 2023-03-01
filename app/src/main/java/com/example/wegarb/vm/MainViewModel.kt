package com.example.wegarb.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.data.WeatherModel


class MainViewModel : ViewModel() {
    val currentLiveDataHeadModel = MutableLiveData<WeatherModel>()
}