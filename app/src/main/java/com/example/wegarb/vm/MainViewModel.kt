package com.example.wegarb.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.data.GarbModel
import com.example.wegarb.data.SearchWeatherModel
import com.example.wegarb.data.WeatherModel
import com.example.wegarb.data.WeatherModelCityName


class MainViewModel() : ViewModel() {
    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()

    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()
    fun setMyModelList(list: MutableList<GarbModel>) {
        mutableRcViewGarbModel.value = list
    }



    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()
    }

