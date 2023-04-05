package com.example.wegarb.presentation.vm

import androidx.lifecycle.*
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.instance.MainDataBase
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.data.models.SearchWeatherModel
import com.example.wegarb.data.models.WeatherModel
import com.example.wegarb.data.models.WeatherModelCityName
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {

    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()

    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()
    fun setMyModelList(list: MutableList<GarbModel>) {
        mutableRcViewGarbModel.value = list
    }
    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()





    private val getDao = mainDataBase.getDao()
    val allInfoModels = getDao.getAllInfoModels().asLiveData()
    fun insertInfoModelInDataBase(infoModel: InfoModel) = viewModelScope.launch {
        getDao.insertInfoModelInDataBase(infoModel)
    }










    class MainViewModelFactory(private val mainDataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(mainDataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

