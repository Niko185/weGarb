package com.example.wegarb.presentation.view.fragments.weather
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.domain.models.cloth_kits.BaseClothesKit
import com.example.wegarb.domain.models.cloth_kits.RainClothesKit
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.data.weather.WeatherRepositoryImpl

import com.example.wegarb.data.weather.remote.api.WeatherApi
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.domain.WeatherRepository
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.example.wegarb.domain.models.name_direction.WindDirection
import com.example.wegarb.domain.models.weather.LocationCityName
import com.example.wegarb.domain.models.weather.SearchWeather
import com.example.wegarb.domain.models.weather.LocationWeather
import com.example.wegarb.presentation.utils.DialogManager
import com.example.wegarb.presentation.utils.GpsDialog
import com.example.wegarb.utils.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*



class WeatherFragment : Fragment(), WeatherAdapter.Listener {
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var weatherApi: WeatherApi
    private lateinit var binding: FragmentAccountBinding
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    private val rainClothesKit: RainClothesKit = RainClothesKit()
    private var listWardrobeElement: List<WardrobeElement>? = null
    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.MainViewModelFactory((requireContext().applicationContext as AppDatabaseInstance).database)
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initLocationClient()
        initRcViewGarb()
        showDataInRcViewOnScreenObserver()
    }

    override fun onResume() {
        super.onResume()
        getMyLocationNow()
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

    }





    @SuppressLint("SetTextI18n")
    private fun getLocationWeatherForecast(latitude: Double, longitude: Double) {



        CoroutineScope(Dispatchers.IO).launch {

            weatherRepository = WeatherRepositoryImpl(weatherApi)
            val weatherResponse = weatherRepository.getLocationWeatherForecast(latitude, longitude)
            val cityNameResponse = weatherRepository.getLocationCityName(latitude, longitude)

            val currentLatitude = weatherResponse.latitude // ok
            val currentLongitude = weatherResponse.longitude // ok

            val date = weatherResponse.date
            val temperature = weatherResponse.temperature
            val windSpeed = weatherResponse.windSpeed
            val feltTemperature = weatherResponse.feltTemperature
            val windDirection = weatherResponse.windDirection
            val humidity = weatherResponse.humidity
           // val description = weatherResponse.descriptionInformationList.get(0)?.feeling
          //  val cityName = cityNameResponse.get(0).name.get("en")


            requireActivity().runOnUiThread {
                val locationWeather = LocationWeather(
                    date = date.toString(),
                    temperature = temperature?.toInt()!!,
                    description = "",
                    windSpeed = windSpeed,
                    latitude = currentLatitude.toString(),
                    longitude = currentLongitude.toString(),
                    ctiy = LocationCityName(""),
                    feltTemperature = feltTemperature?.toInt()!!,
                    windDirection = windDirection.toString(),
                    humidity = humidity.toString()
                )
                weatherViewModel.locationWeather.value = locationWeather



                weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
                    binding.tvCurrentData.text = formatterUnix(it.date)
                    binding.tvCurrentTemperature.text = "${it.temperature}°C"
                    binding.tvCurrentCondition.text = "Direction:  ${it.description}"
                    binding.tvCurrentWind.text = "SearchWindDto speed:  ${it.windSpeed} m/c"
                    binding.tvCurrentCoordinate.text = "(lat:${it.latitude} / lon:${it.longitude})"
                  //  binding.tvCityName.text = "City:  ${it.cityName}"



                    val res = weatherViewModel.locationWeather.value?.temperature?.toInt()
                    val conditionRainResponse = weatherViewModel.locationWeather.value?.description.toString()
                    val conditionRainList = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")
                    if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                        listWardrobeElement = list
                    } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                        val list =
                            weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                        val list =
                            weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                        val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                        listWardrobeElement = list
                    } else {
                        val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                        listWardrobeElement = list
                    }



                    binding.buttonSaveState.setOnClickListener {
                        DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                            override fun onClickComfort() {
                                weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
                                    val historyDayEntity = HistoryDayEntity(
                                        id = null,
                                        date = getDate(),
                                        temperature = it.temperature.toString(),
                                        feltTemperature = it.feltTemperature.toString(),
                                        description = it.description,
                                        windSpeed = it.windSpeed,
                                        windDirection = it.windDirection,
                                        cityName = "",
                                        status = getStatusComfort(),
                                        humidity = it.humidity,
                                        wardrobeElementList = saveWardrobeElementsListInDatabase()
                                    )
                                    weatherViewModel.insertFullDayInformation(historyDayEntity)
                                }
                            }

                            override fun onClickCold() {
                                weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
                                    val historyDayEntity = HistoryDayEntity(
                                        id = null,
                                        date = getDate(),
                                        temperature = it.temperature.toString(),
                                        feltTemperature = it.feltTemperature.toString(),
                                        description = it.description,
                                        windSpeed = it.windSpeed,
                                        windDirection = it.windDirection,
                                        cityName = "",
                                        status = getStatusCold(),
                                        humidity = it.humidity,
                                        wardrobeElementList = saveWardrobeElementsListInDatabase()
                                    )
                                    weatherViewModel.insertFullDayInformation(historyDayEntity)
                                }
                            }

                            override fun onClickHot() {
                                weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
                                    val historyDayEntity = HistoryDayEntity(
                                        id = null,
                                        date = getDate(),
                                        temperature =it.temperature.toString(),
                                        feltTemperature = it.feltTemperature.toString(),
                                        description = it.description,
                                        windSpeed = it.windSpeed,
                                        windDirection = it.windDirection,
                                        cityName =  "",
                                        status = getStatusHot(),
                                        humidity = it.humidity,
                                        wardrobeElementList = saveWardrobeElementsListInDatabase()
                                    )
                                    weatherViewModel.insertFullDayInformation(historyDayEntity)
                                }
                            }
                        })
                    }
                }



                 binding.headCard.setOnClickListener {
                     DialogManager.showHeadDialog(requireContext(), locationWeather)
                 }



            }
        }
    }



    @SuppressLint("SetTextI18n")
    private fun saveWardrobeElementsListInDatabase(): List<WardrobeElement> {

            val resSave = weatherViewModel.locationWeather.value?.temperature?.toInt()
            val conditionRainResponseSave = weatherViewModel.locationWeather.value?.description.toString()
            val conditionRainListSave = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain"
            )

            if (resSave in -60..-35 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                listWardrobeElement = list
            } else if (resSave in -60..-35 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave !in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave in conditionRainListSave) {
                val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                listWardrobeElement = list
            } else {
                val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                listWardrobeElement = list
            }

        return listWardrobeElement!!
    }















    @SuppressLint("SetTextI18n")
    fun getSearchWeatherForecast(cityName: String) {



        CoroutineScope(Dispatchers.IO).launch {
            val response = weatherRepository.getSearchWeatherForecast(cityName)



            val date = response.date //ok
            val city = response.cityName // ok

            val temperature = response.temperature //ok ok
            val feltTemperature = response.feltTemperature // ok ok
            val humidity = response.humidity // ok ok

        //    val description = response.descriptionInfo?.get(0)?.feeling // ok ok


            val currentLatitude = response.currentLatitude // ok ok
            val currentLongitude = response.currentLongitude // ok ok

            val windDirection = response.windDirection // ok
            val windSpeed = response.windSpeed // ok




            requireActivity().runOnUiThread {

                val searchWeather = SearchWeather(
                    date = date.toString(),
                    temperature = temperature?.toInt()!! - 273,
                    description = "",
                    windSpeed = windSpeed,
                    currentLatitude = currentLatitude.toString(),
                    currentLongitude = currentLongitude.toString(),
                    cityName = city.toString(),
                    feltTemperature = feltTemperature?.toInt()!! - 273,
                    windDirection = windDirection.toString(),
                    humidity = humidity.toString()
                )
                weatherViewModel.searchWeather.value = searchWeather



              weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
                binding.tvCurrentData.text = formatterUnix(it.date)
                binding.tvCurrentTemperature.text = "${it.temperature}°C"
                binding.tvCurrentCondition.text = "Direction:  ${it.description}"
                binding.tvCurrentWind.text = "SearchWindDto speed:  ${it.windSpeed} m/c"
                binding.tvCurrentCoordinate.text = "(lat:${it.currentLatitude} / lon:${it.currentLongitude})"
                binding.tvCityName.text = "City:  ${it.cityName}"




                val res = weatherViewModel.searchWeather.value?.temperature
                val conditionRainResponse = weatherViewModel.searchWeather.value?.description.toString()
                val conditionRainList = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")
                if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                    listWardrobeElement = list
                } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                    listWardrobeElement = list
                } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                    listWardrobeElement = list
                } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                    listWardrobeElement = list
                } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                    listWardrobeElement = list
                } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                    listWardrobeElement = list
                } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                    listWardrobeElement = list
                } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                    listWardrobeElement = list
                } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                    listWardrobeElement = list
                } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                    val list =
                        weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                    listWardrobeElement = list
                } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                    listWardrobeElement = list
                } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                    val list =
                        weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                    listWardrobeElement = list
                } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                    listWardrobeElement = list
                } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                    listWardrobeElement = list
                } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                    listWardrobeElement = list
                } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                    listWardrobeElement = list
                } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                    listWardrobeElement = list
                } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                    listWardrobeElement = list
                } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                    listWardrobeElement = list
                } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                    val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                    listWardrobeElement = list
                } else {
                    val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                    listWardrobeElement = list
                }




                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {
                            weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
                                val historyDayEntity = HistoryDayEntity(
                                    id = null,
                                    date = getDate(),
                                    temperature = it.temperature.toString(),
                                    feltTemperature = it.feltTemperature.toString(),
                                    description = it.description,
                                    windSpeed = it.windSpeed,
                                    windDirection = it.windDirection,
                                    cityName = it.cityName,
                                    status = getStatusComfort(),
                                    humidity = it.humidity,
                                    wardrobeElementList = saveSearchWardrobeElementsListInDatabase()
                                )
                                weatherViewModel.insertFullDayInformation(historyDayEntity)
                            }
                        }

                        override fun onClickCold() {
                            weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
                                val historyDayEntity = HistoryDayEntity(
                                    id = null,
                                    date = getDate(),
                                    temperature = it.temperature.toString(),
                                    feltTemperature = it.feltTemperature.toString(),
                                    description = it.description,
                                    windSpeed = it.windSpeed,
                                    windDirection = it.windDirection,
                                    cityName = it.cityName,
                                    status = getStatusCold(),
                                    humidity = it.humidity,
                                    wardrobeElementList = saveSearchWardrobeElementsListInDatabase()
                                )
                                weatherViewModel.insertFullDayInformation(historyDayEntity)
                            }
                        }

                        override fun onClickHot() {
                            weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
                                val historyDayEntity = HistoryDayEntity(
                                    id = null,
                                    date = getDate(),
                                    temperature =it.temperature.toString(),
                                    feltTemperature = it.feltTemperature.toString(),
                                    description = it.description,
                                    windSpeed = it.windSpeed,
                                    windDirection = it.windDirection,
                                    cityName =  it.cityName,
                                    status = getStatusHot(),
                                    humidity = it.humidity,
                                    wardrobeElementList = saveSearchWardrobeElementsListInDatabase()
                                )
                                weatherViewModel.insertFullDayInformation(historyDayEntity)
                            }
                        }
                    })
              } }


                binding.headCard.setOnClickListener {
                    DialogManager.showHeadDialogSearch(requireContext(), searchWeather)
                }


            }
        }
    }



    @SuppressLint("SetTextI18n")
    private fun saveSearchWardrobeElementsListInDatabase(): List<WardrobeElement> {

        val resSave = weatherViewModel.searchWeather.value?.temperature
        val conditionRainResponseSave = weatherViewModel.searchWeather.value?.description.toString()
        val conditionRainListSave = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain"
        )


        /*val list =*/ if(resSave in -60..-35 && conditionRainResponseSave !in conditionRainListSave) {
            weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
        } else if (resSave in -60..-35 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
            listWardrobeElement = list
        } else if (resSave in -34..-27 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
            listWardrobeElement = list
        } else if (resSave in -34..-27 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
            listWardrobeElement = list
        } else if (resSave in -26..-15 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
            listWardrobeElement = list
        } else if (resSave in -26..-15 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
            listWardrobeElement = list
        } else if (resSave in -14..-5 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
            listWardrobeElement = list
        } else if (resSave in -14..-5 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
            listWardrobeElement = list
        } else if (resSave in -4..8 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
            listWardrobeElement = list
        } else if (resSave in -4..8 && conditionRainResponseSave in conditionRainListSave) {
            val list =
                weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
            listWardrobeElement = list
        } else if (resSave in 9..14 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
            listWardrobeElement = list
        } else if (resSave in 9..14 && conditionRainResponseSave in conditionRainListSave) {
            val list =
                weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
            listWardrobeElement = list
        } else if (resSave in 15..18 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
            listWardrobeElement = list
        } else if (resSave in 15..18 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
            listWardrobeElement = list
        } else if (resSave in 19..24 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
            listWardrobeElement = list
        } else if (resSave in 19..24 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
            listWardrobeElement = list
        } else if (resSave in 25..30 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
            listWardrobeElement = list
        } else if (resSave in 25..30 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
            listWardrobeElement = list
        } else if (resSave in 31..55 && conditionRainResponseSave !in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
            listWardrobeElement = list
        } else if (resSave in 31..55 && conditionRainResponseSave in conditionRainListSave) {
            val list = weatherViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
            listWardrobeElement = list
        } else {
            val list = weatherViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)

        }
       // listWardrobeElement = list
        return listWardrobeElement!!
    }





    private fun initRcViewGarb() = with(binding) {
        rcViewGarb.layoutManager = LinearLayoutManager(activity)
        weatherAdapter = WeatherAdapter(this@WeatherFragment)
        rcViewGarb.adapter = weatherAdapter
    }

    private fun showDataInRcViewOnScreenObserver() {
        weatherViewModel.wardrobeElementLists.observe(viewLifecycleOwner) {
            weatherAdapter.submitList(it)
        }
    }






    private fun isGpsEnable(): Boolean {
        val gpsCheck = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gpsCheck.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            responsePermissionDialog()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun responsePermissionDialog() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            }
    }

    private fun initLocationClient() {
        locationClientLauncher = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    internal fun getMyLocationNow() {
        if (isGpsEnable()) {
            getMyLocationCoordinate()
        } else {
            GpsDialog.startDialog(requireContext(), object : GpsDialog.ActionWithUser {
                override fun transferUserGpsSettings() {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun getMyLocationCoordinate() {
        val cancellationToken = CancellationTokenSource()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationClientLauncher.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationToken.token
        )
            .addOnCompleteListener {task ->

                initRetrofit()
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    getLocationWeatherForecast(location.latitude, location.longitude)
                } else {
                    val latitude = 58.0373
                    val longitude = 56.0381
                    getLocationWeatherForecast(latitude, longitude)
                }
            }
    }





    // Вспомогательные функции
    override fun onClickItem(wardrobeElement: WardrobeElement) {
        DialogManager.showClothDialog(requireContext(), wardrobeElement)
    }

    private fun formatterUnix(unixTime: String): String {
        val unixSeconds = unixTime.toLong()
        val date = Date(unixSeconds * 1000)
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = sdf.format(date)
        return formattedDate.toString()
    }

    fun getDate(): String {
        val systemCalendar = Calendar.getInstance()
        return dateFormatter.format(systemCalendar.time)
    }

    fun getStatusComfort(): String {
        return "Was Comfort"
    }
    fun getStatusCold(): String {
        return "Was Cold"
    }

    fun getStatusHot(): String {
        return "Was Hot"
    }

    internal fun getWindDirection(currentWindDeg: Int): String {
        var statusWind: String? = null
        if(currentWindDeg in 349 ..361 || currentWindDeg in 0 .. 11 ) {
            statusWind = WindDirection.NORTH.toString()
        } else if(currentWindDeg in 12 .. 56) {
            statusWind = WindDirection.NORTH_EAST.toString()
        } else if(currentWindDeg in 57 .. 123) {
            statusWind = "East"
        } else if(currentWindDeg in 124 .. 168) {
            statusWind = "South/East"
        } else if(currentWindDeg in 169 .. 213) {
            statusWind = "South"
        } else if(currentWindDeg in 214 .. 258) {
            statusWind = "South/West"
        } else if(currentWindDeg in 259 .. 303) {
            statusWind = "West"
        } else if(currentWindDeg in 304 .. 348){
            statusWind = "North/West"
        } else statusWind = "Sorry, wind direction not found"
        return statusWind.toString()
    }

    companion object {
        @JvmStatic
        fun newInstance() = WeatherFragment()
    }
}









