package com.example.wegarb.presentation.view.fragments.weather


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.*
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.data.WeatherRepositoryImpl
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
import com.example.wegarb.data.HistoryRepositoryImpl
import com.example.wegarb.domain.HistoryRepository
import com.example.wegarb.domain.models.cloth.BaseClothesKit
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.models.weather.Weather
import com.example.wegarb.presentation.utils.SearchCityDialog
import com.example.wegarb.presentation.utils.WardrobeElementDialog
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Suppress ("UNCHECKED_CAST")
class WeatherViewModel(appDatabase: AppDatabase) : ViewModel(), SearchCityDialog.Listener {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherApi: WeatherApi
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    val locationWeather = MutableLiveData<LocationWeather>()
    val searchWeather = MutableLiveData<SearchWeather>()
    val clothingList = MutableLiveData<List<WardrobeElement>>()
    val fullDayInformation = MutableLiveData<HistoryDay>()
    private var type: String = "location"


    private lateinit var historyRepository: HistoryRepository

    init {
        initHistoryRepository(appDatabase)
    }

    private fun initHistoryRepository(appDatabase: AppDatabase) {
        val historyDayDao = appDatabase.historyDayDao()
        historyRepository = HistoryRepositoryImpl(historyDayDao)
    }

    val historyDays: LiveData<List<HistoryDay>> = historyRepository.getAllHistoryDaysDomain()
    fun saveHistoryDay(historyDay: HistoryDay){
        viewModelScope.launch ( Dispatchers.IO ) {
            historyRepository.saveDayInHistoryDomain(historyDay)
        }
    }
    fun deleteHistoryDay(historyDay: HistoryDay){
        viewModelScope.launch (Dispatchers.IO){
            historyRepository.deleteDayFromHistory(historyDay)
        }
    }

   private fun initRetrofit() {
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
            val historyDay = HistoryDay(
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
            saveHistoryDay(historyDay)
        }
    }

    fun getAdditionalWeather(): Weather {
        val typeAdditionalWeather = if (type == "location") {
            locationWeather.value
        } else searchWeather.value
        return typeAdditionalWeather!!
    }

    fun openWardrobeElementDialog(context: Context, wardrobeElement: WardrobeElement){
        WardrobeElementDialog.start(context, wardrobeElement)
        WardrobeElementDialog.getDescription(context, wardrobeElement)
    }

    fun onClickButtonSearchCity() {
        type = "search"
    }

    override fun searchCity(cityName: String?) {
        cityName.let {
            getSearchWeather(cityName.toString())
            type = "search"
        }
    }

    fun onGetCurrentLocationResult(isSucsessfull: Boolean, location: Location) {
        if (isSucsessfull) {
            initRetrofit()
            getLocationWeather(location.latitude, location.longitude)
        } else {
            getLocationWeather(latitude = 00.5454, longitude = 00.3232)
        }
        type = "location"
    }

    private fun getDate(): String {
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

