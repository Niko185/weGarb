package com.example.wegarb.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.data.WeatherModel
import com.example.wegarb.data.WeatherModelCityName


class MainViewModel : ViewModel() {
    val currentLiveDataHeadModel = MutableLiveData<WeatherModel>()
    val cityName = MutableLiveData<WeatherModelCityName>()
}