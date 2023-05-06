package com.example.wegarb.presentation.vm

import androidx.lifecycle.*
import com.example.wegarb.data.database.entity.FullDayInformation
import com.example.wegarb.data.database.instance.MainDataBase
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.main.common.WardrobeElement
import com.example.wegarb.domain.models.main.search_request.show_search_response.WeatherForecastSearch
import com.example.wegarb.domain.models.main.coordinate_request.show_response.WeatherForecast
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {
    private val getDao = mainDataBase.getDao()


    val weatherForecast = MutableLiveData<WeatherForecast>()
    val weatherForecastSearch = MutableLiveData<WeatherForecastSearch>()


    val wardrobeElementLists = MutableLiveData<MutableList<WardrobeElement>>()
    fun getListWardrobeElements(list: MutableList<WardrobeElement>): MutableList<WardrobeElement> {
        wardrobeElementLists.value = list
        return list
    }


    fun insertFullDayInformation(fullDayInformation: FullDayInformation) = viewModelScope.launch {
        getDao.insertFullDayInformation(fullDayInformation)
    }

    fun deleteFullDayInformation(fullDayInformation: FullDayInformation) = viewModelScope.launch {
        getDao.deleteFullDayInformation(fullDayInformation)
    }

    val getAllFullDaysInformation = getDao.getAllFullDaysInformation().asLiveData()

    val savedFullDaysInformation = MutableLiveData<FullDayInformation>()


    class MainViewModelFactory(private val mainDataBase: MainDataBase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(mainDataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

