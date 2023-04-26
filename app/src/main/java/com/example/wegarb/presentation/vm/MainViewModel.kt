package com.example.wegarb.presentation.vm

import androidx.lifecycle.*
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.instance.MainDataBase
import com.example.wegarb.data.models.*
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {
    private val getDao = mainDataBase.getDao()

    var mutableHeadModel = MutableLiveData<HeadModel>()
    val mutableHeadCardWeatherModel = MutableLiveData<WeatherModel>()
    val mutableHeadCardWeatherModelCity = MutableLiveData<WeatherModelCityName>()
    val mutableHeadCardSearchModel = MutableLiveData<SearchWeatherModel>()


    val mutableRcViewGarbModel = MutableLiveData<MutableList<GarbModel>>()

    fun setMyModelList(list: MutableList<GarbModel>): MutableList<GarbModel> {
        mutableRcViewGarbModel.value = list
        return list
    }


    fun insertInfoModelInDataBase(infoModel: InfoModel) = viewModelScope.launch {
        getDao.insertInfoModelInDataBase(infoModel)
    }

    fun deleteInfoModelFromDataBase(infoModel: InfoModel) = viewModelScope.launch {
        getDao.deleteInfoModelFromDatabase(infoModel)
    }

    val getAllInfoModels = getDao.getAllInfoModels().asLiveData()

    var mutableSavedModel = MutableLiveData<InfoModel>()


    class MainViewModelFactory(private val mainDataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(mainDataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

