package com.example.wegarb.presentation.view.fragments.weather


import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.*
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.data.weather.WeatherRepositoryImpl
import com.example.wegarb.data.weather.remote.api.WeatherApi
import com.example.wegarb.domain.WeatherRepository
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth.element_kit.WardrobeElement
import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.models.weather.LocationWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.viewModelScope
import com.example.wegarb.domain.models.cloth.BaseClothesKit
import com.example.wegarb.domain.models.weather.Weather
import com.example.wegarb.presentation.utils.WardrobeElementDialog
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Suppress ("UNCHECKED_CAST")
class WeatherViewModel(appDatabase: AppDatabase) : ViewModel() {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherApi: WeatherApi
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    private val historyDayDao = appDatabase.historyDayDao()
    val locationWeather = MutableLiveData<LocationWeather>()
    val searchWeather = MutableLiveData<SearchWeather>()
    val clothingList = MutableLiveData<List<WardrobeElement>>()
    val fullDayInformation = MutableLiveData<HistoryDayEntity>()
    val historyDayList = historyDayDao.getAllHistoryDays().asLiveData()
    var type: String = "location"

    private fun saveFullDayInformation(historyDayEntity: HistoryDayEntity) = viewModelScope.launch {
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
            val cityNameString = cityNameResponse.get(0).toString()

            val locationWeatherData = LocationWeather(
                date = weatherResponse.date,
                temperature = weatherResponse.temperature,
                description = weatherResponse.description,
                windSpeed = weatherResponse.windSpeed,
                latitude = weatherResponse.latitude,
                longitude = weatherResponse.longitude,
                city = cityNameString.substring(22, cityNameString.length - 1),
                feltTemperature = weatherResponse.feltTemperature,
                windDirection = weatherResponse.windDirection,
                humidity = weatherResponse.humidity
            )
            locationWeather.postValue(locationWeatherData)
            getClothesKitForShow(locationWeatherData)
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
                latitude = responseSearch.latitude,
                longitude = responseSearch.longitude,
                city = responseSearch.city,
                feltTemperature = responseSearch.feltTemperature,
                windDirection = responseSearch.windDirection,
                humidity = responseSearch.humidity
            )
            searchWeather.postValue(searchWeatherData)
            getClothesKitForShow(searchWeatherData)
        }
    }

    private fun getClothesKitForShow(weather: Weather) {
        val list = when(weather.temperature) {
            in -60..-35 -> baseClothesKit.kitHardCold
            in -34..-27 -> baseClothesKit.kitSuperCold
            in -26..-15 -> baseClothesKit.kitVeryCold
            in -14..-5 -> baseClothesKit.kitNormalCold
            in -4..8 -> baseClothesKit.kitTransitCold
            in 9..14 -> baseClothesKit.kitTransitHot
            in 15..18 -> baseClothesKit.kitNormalHot
            in 19..24 -> baseClothesKit.kitVeryHot
            in 25..30 -> baseClothesKit.kitSuperHot
            in 31..55 -> baseClothesKit.kitHardHot
            else -> listOf()
        }
        clothingList.postValue(list)
    }

    private fun getClothKitForSave(): List<WardrobeElement> {
        val list = clothingList.value
        return list!!
    }

    fun onClickSaveHistoryDay(status: String) {
        val typeWeather = when (type) {
            "location" -> locationWeather.value
            "search" -> searchWeather.value
            else -> null
        }

        if (typeWeather != null) {
            val historyDay = HistoryDayEntity(
                id = null,
                date = getDate(),
                temperature = typeWeather.temperature.toString(),
                feltTemperature =typeWeather.feltTemperature.toString(),
                description = typeWeather.description,
                windSpeed = typeWeather.windSpeed,
                windDirection = typeWeather.windDirection,
                cityName = typeWeather.city,
                status = status,
                humidity = typeWeather.humidity,
                clothingList = getClothKitForSave()
            )
            saveFullDayInformation(historyDay)
        }
    }

    fun openDialog(context: Context, wardrobeElement: WardrobeElement){
        WardrobeElementDialog.start(context, wardrobeElement)
        WardrobeElementDialog.getDescription(context, wardrobeElement)
    }

    private  fun getDate(): String {
        val systemCalendar = Calendar.getInstance()
        return dateFormatter.format(systemCalendar.time)
    }

    fun formatterUnix(unixTime: String): String {
        val unixSeconds = unixTime.toLong()
        val date = Date(unixSeconds * 1000)
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = sdf.format(date)
        return formattedDate.toString()
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

