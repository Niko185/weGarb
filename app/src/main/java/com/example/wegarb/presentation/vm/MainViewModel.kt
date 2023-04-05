package com.example.wegarb.presentation.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wegarb.data.database.instance.MainDataBase
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.data.models.SearchWeatherModel
import com.example.wegarb.data.models.WeatherModel
import com.example.wegarb.data.models.WeatherModelCityName

@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {

    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()

    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()
    fun setMyModelList(list: MutableList<GarbModel>) {
        mutableRcViewGarbModel.value = list
    }
    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()


    class MainViewModelFactory(private val mainDataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(mainDataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

