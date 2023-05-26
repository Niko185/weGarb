package com.example.wegarb.presentation.view.fragments.weather


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
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
import com.example.wegarb.presentation.utils.DialogManager
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
        Log.e("MyLog", "getLocationWeather")
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
                city = "City",
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

    private fun getLocationClothKitForSave(): List<WardrobeElement> {
        val list = clothingList.value
        return list!!
    }

    private fun getSearchClothKitForSave(): List<WardrobeElement> {
       val list = clothingList.value
        return list!!
    }

    fun onClickSaveLocationDayDialog(status: String) {
            val historyDay = HistoryDayEntity(
                id = null,
                date = getDate(),
                temperature = locationWeather.value?.temperature.toString(),
                feltTemperature = locationWeather.value?.feltTemperature.toString(),
                description = locationWeather.value?.description.toString(),
                windSpeed = locationWeather.value?.windSpeed.toString(),
                windDirection = locationWeather.value?.windDirection.toString(),
                cityName = locationWeather.value?.city ?: "not found city name",
                status = status,
                humidity = locationWeather.value?.humidity.toString(),
                clothingList = getLocationClothKitForSave()
            )
            saveFullDayInformation(historyDay)
    }

    fun onClickSaveSearchDayDialog(status: String){
            val historyDay = HistoryDayEntity(
                id = null,
                date = getDate(),
                temperature = searchWeather.value?.temperature.toString(),
                feltTemperature = searchWeather.value?.feltTemperature.toString(),
                description = searchWeather.value?.description.toString(),
                windSpeed = searchWeather.value?.windSpeed.toString(),
                windDirection = searchWeather.value?.windDirection.toString(),
                cityName = searchWeather.value?.city.toString(),
                status = status,
                humidity = searchWeather.value?.humidity.toString(),
                clothingList = getSearchClothKitForSave()
            )
            saveFullDayInformation(historyDay)
    }

    fun openDialog(context: Context, wardrobeElement: WardrobeElement){
        DialogManager.showClothDialog(context, wardrobeElement)
        DialogManager.getDescriptionCloth(context, wardrobeElement)
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

