package com.example.wegarb.presentation.view.fragments.weather

import androidx.lifecycle.*
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.data.weather.WeatherRepositoryImpl
import com.example.wegarb.data.weather.remote.api.WeatherApi
import com.example.wegarb.domain.WeatherRepository
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.models.weather.LocationWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.viewModelScope
import com.example.wegarb.domain.models.cloth_kits.BaseClothesKit
import com.example.wegarb.domain.models.cloth_kits.RainClothesKit
import java.text.SimpleDateFormat
import java.util.*

@Suppress ("UNCHECKED_CAST")
class WeatherViewModel(appDatabase: AppDatabase) : ViewModel() {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherApi: WeatherApi
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    private val rainClothesKit: RainClothesKit = RainClothesKit()
    private val historyDayDao = appDatabase.historyDayDao()
    val locationWeather = MutableLiveData<LocationWeather>()
    val searchWeather = MutableLiveData<SearchWeather>()
    val wardrobeElementLists = MutableLiveData<List<WardrobeElement>>()
    val savedFullDaysInformation = MutableLiveData<HistoryDayEntity>()
    val getAllDaysHistory = historyDayDao.getAllHistoryDays().asLiveData()

    private fun getListWardrobeElements(list: List<WardrobeElement>): List<WardrobeElement> {
        wardrobeElementLists.value = list
        return list
    }

    private fun insertFullDayInformation(historyDayEntity: HistoryDayEntity) = viewModelScope.launch {
        historyDayDao.insertHistoryDay(historyDayEntity)
    }

    fun deleteFullDayInformation(historyDayEntity: HistoryDayEntity) = viewModelScope.launch {
        historyDayDao.deleteHistoryDay(historyDayEntity)
    }

     fun initRetrofit() {
        val interceptorInstance = HttpLoggingInterceptor()
        interceptorInstance.level = HttpLoggingInterceptor.Level.BODY

        val clientInstance = OkHttpClient.Builder()
            .addInterceptor(interceptorInstance)
            .build()

        val retrofitInstance = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/").client(clientInstance)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

         weatherApi = retrofitInstance.create(WeatherApi::class.java)
         weatherRepository = WeatherRepositoryImpl(weatherApi)
    }

    fun getLocationWeather(latitude: Double, longitude: Double)  {
        viewModelScope.launch(Dispatchers.IO) {
            val weatherResponse = weatherRepository.getLocationWeatherForecast(latitude, longitude)
            val cityNameResponse = weatherRepository.getLocationCityName(latitude, longitude)

            val locationWeatherData = LocationWeather(
                date = weatherResponse.date,
                temperature = weatherResponse.temperature,
                description = weatherResponse.description,
                windSpeed = weatherResponse.windSpeed,
                latitude = weatherResponse.latitude,
                longitude = weatherResponse.longitude,
                ctiy = cityNameResponse[0],
                feltTemperature = weatherResponse.feltTemperature,
                windDirection = weatherResponse.windDirection,
                humidity = weatherResponse.humidity
            )
            locationWeather.postValue(locationWeatherData)
        }
    }

    fun getSearchWeather(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val responseSearch = weatherRepository.getSearchWeatherForecast(cityName)

            val searchWeatherData = SearchWeather(
                date = responseSearch.date,
                temperature = responseSearch.temperature,
                description = responseSearch.description,
                windSpeed =  responseSearch.windSpeed,
                currentLatitude = responseSearch.currentLatitude,
                currentLongitude = responseSearch.currentLongitude,
                cityName = responseSearch.cityName,
                feltTemperature = responseSearch.feltTemperature,
                windDirection = responseSearch.windDirection,
                humidity = responseSearch.humidity
            )
            searchWeather.postValue(searchWeatherData)
        }
    }

    fun getLocationClothKit(): List<WardrobeElement> {
        val clothesList = mutableListOf<WardrobeElement>()

        val res = locationWeather.value?.temperature
        val conditionRainResponse = locationWeather.value?.description
        val conditionRainList = mutableListOf("Rain")
        val selectedClothesKit = when {
            res in -60..-35 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
            res in -34..-27 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
            res in -26..-15 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
            res in -14..-5 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
            res in -4..8 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
            res in 9..14 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
            res in 15..18 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
            res in 19..24 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
            res in 25..30 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
            res in 31..55 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot
            else -> null
        }
        if (selectedClothesKit != null) {
            clothesList.addAll(
                if (conditionRainResponse !in conditionRainList) {
                    getListWardrobeElements(selectedClothesKit.first)
                } else {
                    getListWardrobeElements(selectedClothesKit.second)
                }
            )
        }

        return clothesList
    }

    fun getSearchClothKit(): List<WardrobeElement> {
        val clothesList = mutableListOf<WardrobeElement>()

        val res = searchWeather.value?.temperature
        val conditionRainResponse = searchWeather.value?.description
        val conditionRainList = mutableListOf("Rain")
        val selectedClothesKit = when {
            res in -60..-35 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
            res in -34..-27 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
            res in -26..-15 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
            res in -14..-5 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
            res in -4..8 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
            res in 9..14 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
            res in 15..18 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
            res in 19..24 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
            res in 25..30 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
            res in 31..55 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot
            else -> null
        }
        if (selectedClothesKit != null) {
            clothesList.addAll(
                if (conditionRainResponse !in conditionRainList) {
                    getListWardrobeElements(selectedClothesKit.first)
                } else {
                    getListWardrobeElements(selectedClothesKit.second)
                }
            )
        }
        return clothesList
    }


    fun onClickSaveLocationDayDialog(status: String) {
        val historyDayEntity = HistoryDayEntity(
            id = null,
            date = getDate(),
            temperature = locationWeather.value?.temperature.toString(),
            feltTemperature = locationWeather.value?.feltTemperature.toString(),
            description = locationWeather.value?.description.toString(),
            windSpeed = locationWeather.value?.windSpeed.toString(),
            windDirection =locationWeather.value?.windDirection.toString(),
            cityName = locationWeather.value?.ctiy?.name ?: "not found city name",
            status = status,
            humidity = locationWeather.value?.humidity.toString(),
            wardrobeElementList = getLocationClothKit()
        )
        insertFullDayInformation(historyDayEntity)
    }

    fun onClickSaveSearchDayDialog(status: String){
        val historyDayEntity = HistoryDayEntity(
            id = null,
            date = getDate(),
            temperature = searchWeather.value?.temperature.toString(),
            feltTemperature = searchWeather.value?.feltTemperature.toString(),
            description = searchWeather.value?.description.toString(),
            windSpeed = searchWeather.value?.windSpeed.toString(),
            windDirection =searchWeather.value?.windDirection.toString(),
            cityName = searchWeather.value?.cityName.toString(),
            status = status,
            humidity = searchWeather.value?.humidity.toString(),
            wardrobeElementList = getLocationClothKit()
        )
        insertFullDayInformation(historyDayEntity)
    }

    private  fun getDate(): String {
        val systemCalendar = Calendar.getInstance()
        return dateFormatter.format(systemCalendar.time)
    }

    class WeatherViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(appDatabase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

