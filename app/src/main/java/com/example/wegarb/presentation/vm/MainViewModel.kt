package com.example.wegarb.presentation.vm

import androidx.lifecycle.*
import com.example.wegarb.data.database.entity.FullDayInformation
import com.example.wegarb.data.database.instance.MainDataBase
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.newvariant.additional.AdditionalInformation
import com.example.wegarb.domain.models.newvariant.garb.WardrobeElement
import com.example.wegarb.domain.models.newvariant.searching.show.CurrentWeatherSearching
import com.example.wegarb.domain.models.newvariant.weather.show.CurrentWeather
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class MainViewModel(mainDataBase: MainDataBase) : ViewModel() {
    private val getDao = mainDataBase.getDao()


    val currentWeather = MutableLiveData<CurrentWeather>()
    val currentWeatherSearching = MutableLiveData<CurrentWeatherSearching>()
    val additionalInformation = MutableLiveData<AdditionalInformation>()

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

