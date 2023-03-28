package com.example.wegarb.presentation.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.data.models.SearchWeatherModel
import com.example.wegarb.data.models.WeatherModel
import com.example.wegarb.data.models.WeatherModelCityName


class MainViewModel() : ViewModel() {

    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()

    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()
    fun setMyModelList(list: MutableList<GarbModel>) {
        mutableRcViewGarbModel.value = list
    }

    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()


}

