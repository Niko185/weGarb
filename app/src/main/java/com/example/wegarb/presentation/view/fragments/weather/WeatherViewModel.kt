package com.example.wegarb.presentation.view.fragments.weather


import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.*
import com.example.wegarb.domain.repository.WeatherRepository
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth.single_wardrobe_element.WardrobeElement
import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.models.weather.LocationWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.wegarb.domain.repository.HistoryRepository
import com.example.wegarb.domain.models.cloth.kits.BaseClothesKit
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.models.weather.Weather
import com.example.wegarb.presentation.dialogs.SearchCityDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@SuppressLint("SimpleDateFormat")
@Suppress ("UNCHECKED_CAST")
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModel(), SearchCityDialog.HandlerRequest {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    val locationWeather = MutableLiveData<LocationWeather>()
    val searchWeather = MutableLiveData<SearchWeather>()
    val clothingList = MutableLiveData<List<WardrobeElement>>()
    private var type: String = "location"

    fun getMyLocationCoordinateRealization(isSucsessfull: Boolean, location: Location) {
        if (isSucsessfull) {
            getLocationWeather(location.latitude, location.longitude)
        } else {
            getLocationWeather(latitude = 00.5454, longitude = 00.3232)
        }
        type = "location"
    }

    override fun getWeatherForCityRealization(cityName: String?) {
        cityName.let {
            getSearchWeather(cityName.toString())
            type = "search"
        }
    }

    private fun getLocationWeather(latitude: Double, longitude: Double)  {
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

   private fun getSearchWeather(cityName: String) {
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

    private fun saveDayInHistory(historyDay: HistoryDay) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.saveDayInHistory(historyDay)
        }
    }

    fun saveDayInHistoryRealization(status: String) {
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
                clothingList = getClothKitForSave()
            )
           saveDayInHistory(historyDay)
        }
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

    fun getWindDirectionName(windDirection: Int): String {
        val statusWind: String?
        if(windDirection in 349 ..361 || windDirection in 0 .. 11 ) {
            statusWind = "North"
        } else if(windDirection in 12 .. 56) {
            statusWind = "North/East"
        } else if(windDirection in 57 .. 123) {
            statusWind = "East"
        } else if(windDirection in 124 .. 168) {
            statusWind = "South/East"
        } else if(windDirection in 169 .. 213) {
            statusWind = "South"
        } else if(windDirection in 214 .. 258) {
            statusWind = "South/West"
        } else if(windDirection in 259 .. 303) {
            statusWind = "West"
        } else if(windDirection in 304 .. 348){
            statusWind = "North/West"
        } else statusWind = "Sorry, wind direction not found"
        return statusWind.toString()
    }
}




