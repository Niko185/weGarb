package com.example.wegarb.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.data.GarbModel
import com.example.wegarb.data.SearchWeatherModel
import com.example.wegarb.data.WeatherModel
import com.example.wegarb.data.WeatherModelCityName


class MainViewModel() : ViewModel() {

    init {
        Log.e("Mylog", "init mainViewModel and created viewModel")
    }

    override fun onCleared() {
        Log.e("Mylog", "onCleared mainViewModel and destroy viewModel")
        super.onCleared()
    }


    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()

    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()
    fun setMyModelList(list: MutableList<GarbModel>) {
        mutableRcViewGarbModel.value = list
    }



    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()
    }

