package com.example.wegarb.presentation.vm

import androidx.lifecycle.*
import com.example.wegarb.data.storage.local.history.dto.HistoryDayDto
import com.example.wegarb.data.storage.AppDatabase
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.second.WardrobeElement
import com.example.wegarb.domain.models.SearchWeatherInfo
import com.example.wegarb.domain.models.LocationWeatherInfo
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class MainViewModel(appDatabase: AppDatabase) : ViewModel() {
    private val getDao = appDatabase.historyDayDao()


    val locationWeatherInfo = MutableLiveData<LocationWeatherInfo>()
    val searchWeatherInfo = MutableLiveData<SearchWeatherInfo>()


    val wardrobeElementLists = MutableLiveData<MutableList<WardrobeElement>>()
    fun getListWardrobeElements(list: MutableList<WardrobeElement>): MutableList<WardrobeElement> {
        wardrobeElementLists.value = list
        return list
    }


    fun insertFullDayInformation(historyDayDto: HistoryDayDto) = viewModelScope.launch {
        getDao.insertFullDayInformation(historyDayDto)
    }

    fun deleteFullDayInformation(historyDayDto: HistoryDayDto) = viewModelScope.launch {
        getDao.deleteFullDayInformation(historyDayDto)
    }

    val getAllFullDaysInformation = getDao.getAllFullDaysInformation().asLiveData()

    val savedFullDaysInformation = MutableLiveData<HistoryDayDto>()


    class MainViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(appDatabase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

