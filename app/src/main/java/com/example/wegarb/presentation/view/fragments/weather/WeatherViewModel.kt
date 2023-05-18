package com.example.wegarb.presentation.view.fragments.weather

import androidx.lifecycle.*
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.models.weather.LocationWeather
import kotlinx.coroutines.launch

@Suppress ("UNCHECKED_CAST")
class WeatherViewModel(appDatabase: AppDatabase) : ViewModel() {

    // Для доступа к функциям таблицы historyDay
    private val historyDayDao = appDatabase.historyDayDao()

    // Переопределенная функция из HistoryDayDao
    val getAllFullDaysInformation = historyDayDao.getAllHistoryDays().asLiveData()

    // Переопределенная функция из HistoryDayDao
    fun insertFullDayInformation(historyDayEntity: HistoryDayEntity) = viewModelScope.launch {
        historyDayDao.insertHistoryDay(historyDayEntity)
    }

    // Переопределенная функция из HistoryDayDao
    fun deleteFullDayInformation(historyDayEntity: HistoryDayEntity) = viewModelScope.launch {
        historyDayDao.deleteHistoryDay(historyDayEntity)
    }





    // Для сохранения и отображения сохраненных в historyDayEntity на третьем форагменте
    val savedFullDaysInformation = MutableLiveData<HistoryDayEntity>()

    // Для сохранения и отображения данных погоды на первом фрагменте
    val locationWeather = MutableLiveData<LocationWeather>()

    // Для сохранения и отображения данных погоды по поиску города на первом фрагменте
    val searchWeather = MutableLiveData<SearchWeather>()

    // Для отображения набора одежды в recyclerView на первом фрагменте
    val wardrobeElementLists = MutableLiveData<List<WardrobeElement>>()
    fun getListWardrobeElements(list: List<WardrobeElement>): List<WardrobeElement> {
        wardrobeElementLists.value = list
        return list
    }




    class MainViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(appDatabase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

