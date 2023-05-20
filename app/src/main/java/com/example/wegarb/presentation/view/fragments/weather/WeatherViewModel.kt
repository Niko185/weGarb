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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Suppress ("UNCHECKED_CAST")
class WeatherViewModel(appDatabase: AppDatabase) : ViewModel() {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherApi: WeatherApi

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
    }

    fun getLocationWeather(latitude: Double, longitude: Double)  {

        CoroutineScope(Dispatchers.IO).launch {

            weatherRepository = WeatherRepositoryImpl(weatherApi)
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

        CoroutineScope(Dispatchers.IO).launch {
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





    // Для доступа к функциям Database historyDay
    private val historyDayDao = appDatabase.historyDayDao()

    // Переопределенная функция из HistoryDayDao
    val getAllDaysHistory = historyDayDao.getAllHistoryDays().asLiveData()

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






    class WeatherViewModelFactory(private val appDatabase: AppDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
                return WeatherViewModel(appDatabase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}

