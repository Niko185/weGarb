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
import com.example.wegarb.domain.collections.BaseClothesKit
import com.example.wegarb.domain.collections.RainClothesKit
import com.example.wegarb.data.database.entity.FullDayInformation
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.data.retrofit.MainApi
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.newvariant.additional.AdditionalInformation
import com.example.wegarb.domain.models.newvariant.garb.WardrobeElement
import com.example.wegarb.domain.models.newvariant.searching.show.CurrentWeatherSearching
import com.example.wegarb.domain.models.newvariant.weather.show.CurrentWeather
import com.example.wegarb.presentation.vm.MainViewModel
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
    private lateinit var mainApi: MainApi
    private lateinit var binding: FragmentAccountBinding
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    private val rainClothesKit: RainClothesKit = RainClothesKit()
    private var listWardrobeElement: MutableList<WardrobeElement>? = null
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as MainDataBaseInitialization).mainDataBaseInitialization)
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

        mainApi = retrofitInstance.create(MainApi::class.java)
    }

    @SuppressLint("SetTextI18n")
    private fun getMainWeatherForecast(latitude: Double, longitude: Double) {


            CoroutineScope(Dispatchers.IO).launch {
            val weatherResponse = mainApi.getWeatherForecast(latitude, longitude)
            val cityNameResponse = mainApi.getCityName(latitude, longitude)

            val date = weatherResponse.body()?.currentWeatherForecast?.date
            val temperature = weatherResponse.body()?.currentWeatherForecast?.temperature
            val description = weatherResponse.body()?.currentWeatherForecast?.weather?.get(0)?.description
            val windSpeed = weatherResponse.body()?.currentWeatherForecast?.windSpeed
            val currentLatitude = weatherResponse.body()?.latitude
            val currentLongitude = weatherResponse.body()?.longitude
            val cityName = cityNameResponse.get(0).name.get("es")
            val feltTemperature = weatherResponse.body()?.currentWeatherForecast?.feltTemperature
            val windDirection = weatherResponse.body()?.currentWeatherForecast?.windDirection
            val humidity = weatherResponse.body()?.currentWeatherForecast?.humidity



             requireActivity().runOnUiThread {
                val currentWeather = CurrentWeather(
                    date = date.toString(),
                    temperature = temperature?.toInt()!!,
                    description = description.toString(),
                    windSpeed = windSpeed.toString(),
                    currentLatitude = currentLatitude.toString(),
                    currentLongitude = currentLongitude.toString(),
                    cityName = cityName.toString(),
                    feltTemperature = feltTemperature.toString(),
                    windDirection = windDirection.toString(),
                    humidity = humidity.toString()
                )

                mainViewModel.currentWeather.value = currentWeather
                mainViewModel.currentWeather.observe(viewLifecycleOwner) {
                    binding.tvCurrentData.text = formatterUnix(it.date)
                    binding.tvCurrentTemperature.text = it.temperature.toString()
                    binding.tvCurrentCondition.text = it.description
                    binding.tvCurrentWind.text = it.windSpeed
                    binding.tvCurrentCoordinate.text = "${it.currentLatitude} / ${it.currentLongitude}"
                    binding.tvCityName.text = it.cityName




                    val res = mainViewModel.currentWeather.value?.temperature?.toInt()
                    val conditionRainResponse = mainViewModel.currentWeather.value?.description.toString()
                    val conditionRainList = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")
                    if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                        listWardrobeElement = list
                    } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                        val list =
                            mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                        val list =
                            mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                        listWardrobeElement = list
                    } else {
                        val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                        listWardrobeElement = list
                    }




                    binding.buttonSaveState.setOnClickListener {
                        DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                            override fun onClickComfort() {
                                mainViewModel.currentWeather.observe(viewLifecycleOwner) {
                                    val fullDayInformation = FullDayInformation(
                                        id = null,
                                        date = getDate(),
                                        currentTemp = it.temperature.toString(),
                                        currentFeelsLike = it.feltTemperature,
                                        currentCondition = it.description,
                                        currentWind = it.windSpeed,
                                        windDirection = it.windDirection,
                                        currentCity = it.cityName,
                                        status = getStatusComfort(),
                                        humidity = it.humidity,
                                        garb = saveListInDatabaseCoordinateVariant()
                                    )
                                    mainViewModel.insertFullDayInformation(fullDayInformation)
                                }

                            }

                            override fun onClickCold() {
                                mainViewModel.currentWeather.observe(viewLifecycleOwner) {
                                    val fullDayInformation = FullDayInformation(
                                        id = null,
                                        date = getDate(),
                                        currentTemp = it.temperature.toString(),
                                        currentFeelsLike = it.feltTemperature,
                                        currentCondition = it.description,
                                        currentWind = it.windSpeed,
                                        windDirection = it.windDirection,
                                        currentCity = it.cityName,
                                        status = getStatusCold(),
                                        humidity = it.humidity,
                                        garb = saveListInDatabaseCoordinateVariant()
                                    )
                                    mainViewModel.insertFullDayInformation(fullDayInformation)
                                }
                            }

                            override fun onClickHot() {
                                mainViewModel.currentWeather.observe(viewLifecycleOwner) {
                                    val fullDayInformation = FullDayInformation(
                                        id = null,
                                        date = getDate(),
                                        currentTemp =it.temperature.toString(),
                                        currentFeelsLike = it.feltTemperature,
                                        currentCondition = it.description,
                                        currentWind = it.windSpeed,
                                        windDirection = it.windDirection,
                                        currentCity =  it.cityName,
                                        status = getStatusHot(),
                                        humidity = it.humidity,
                                        garb = saveListInDatabaseCoordinateVariant()
                                    )
                                    mainViewModel.insertFullDayInformation(fullDayInformation)
                                }
                            }
                        })
                    }
                }

                 binding.headCard.setOnClickListener {
                     DialogManager.showHeadDialog(requireContext(), currentWeather)
                 }

             }
             }
    }


    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseCoordinateVariant(): MutableList<WardrobeElement> {

            val resSave = mainViewModel.currentWeather.value?.temperature?.toInt()
            val conditionRainResponseSave = mainViewModel.currentWeather.value?.description.toString()
            val conditionRainListSave = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain"
            )

            if (resSave in -60..-35 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                listWardrobeElement = list
            } else if (resSave in -60..-35 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                listWardrobeElement = list
            } else {
                val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                listWardrobeElement = list
            }

        return listWardrobeElement!!
    }

























    fun getSearchingWeatherForecast(cityName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = mainApi.getWeatherForecastSearching(cityName)

            val date = response.body()?.dt
            val temperature = response.body()?.main?.temp
            val description = response.body()?.weather?.get(0)?.main
            val windSpeed = response.body()?.wind?.speed
            val currentLatitude = response.body()?.coord?.lat
            val currentLongitude = response.body()?.coord?.lon
            val feltTemperature = response.body()?.main?.feels_like
            val windDirection = response.body()?.wind?.deg
            val humidity = response.body()?.main?.humidity
            val city = response.body()?.name


            requireActivity().runOnUiThread {


                val currentWeatherSearching = CurrentWeatherSearching(
                    date = date.toString(),
                    temperature = temperature?.toInt()!!,
                    description = description.toString(),
                    windSpeed = windSpeed.toString(),
                    currentLatitude = currentLatitude.toString(),
                    currentLongitude = currentLongitude.toString(),
                    cityName = city.toString(),
                    feltTemperature = feltTemperature.toString(),
                    windDirection = windDirection.toString(),
                    humidity = humidity.toString()
                )

            mainViewModel.currentWeatherSearching.value = currentWeatherSearching
            mainViewModel.currentWeatherSearching.observe(viewLifecycleOwner) {
                binding.tvCurrentData.text = formatterUnix(it.date)
                binding.tvCurrentTemperature.text = it.temperature.toString()
                binding.tvCurrentCondition.text = it.description
                binding.tvCurrentWind.text = it.windSpeed
                binding.tvCurrentCoordinate.text = "${it.currentLatitude} / ${it.currentLongitude}"
                binding.tvCityName.text = it.cityName






                val res = mainViewModel.currentWeatherSearching.value?.temperature?.toInt()
                val conditionRainResponse = mainViewModel.currentWeatherSearching.value?.description.toString()
                val conditionRainList = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")
                if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
                    listWardrobeElement = list
                } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
                    listWardrobeElement = list
                } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
                    listWardrobeElement = list
                } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
                    listWardrobeElement = list
                } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
                    listWardrobeElement = list
                } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
                    listWardrobeElement = list
                } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
                    listWardrobeElement = list
                } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
                    listWardrobeElement = list
                } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
                    listWardrobeElement = list
                } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                    val list =
                        mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
                    listWardrobeElement = list
                } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
                    listWardrobeElement = list
                } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                    val list =
                        mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
                    listWardrobeElement = list
                } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
                    listWardrobeElement = list
                } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
                    listWardrobeElement = list
                } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
                    listWardrobeElement = list
                } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
                    listWardrobeElement = list
                } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
                    listWardrobeElement = list
                } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
                    listWardrobeElement = list
                } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
                    listWardrobeElement = list
                } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                    val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
                    listWardrobeElement = list
                } else {
                    val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                    listWardrobeElement = list
                }

                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {
                            mainViewModel.currentWeatherSearching.observe(viewLifecycleOwner) {
                                val fullDayInformation = FullDayInformation(
                                    id = null,
                                    date = getDate(),
                                    currentTemp = it.temperature.toString(),
                                    currentFeelsLike = it.feltTemperature,
                                    currentCondition = it.description,
                                    currentWind = it.windSpeed,
                                    windDirection = it.windDirection,
                                    currentCity = it.cityName,
                                    status = getStatusComfort(),
                                    humidity = it.humidity,
                                    garb = saveListInDatabaseSearchVariant()
                                )
                                mainViewModel.insertFullDayInformation(fullDayInformation)
                            }

                        }

                        override fun onClickCold() {
                            mainViewModel.currentWeatherSearching.observe(viewLifecycleOwner) {
                                val fullDayInformation = FullDayInformation(
                                    id = null,
                                    date = getDate(),
                                    currentTemp = it.temperature.toString(),
                                    currentFeelsLike = it.feltTemperature,
                                    currentCondition = it.description,
                                    currentWind = it.windSpeed,
                                    windDirection = it.windDirection,
                                    currentCity = it.cityName,
                                    status = getStatusCold(),
                                    humidity = it.humidity,
                                    garb = saveListInDatabaseSearchVariant()
                                )
                                mainViewModel.insertFullDayInformation(fullDayInformation)
                            }
                        }

                        override fun onClickHot() {
                            mainViewModel.currentWeatherSearching.observe(viewLifecycleOwner) {
                                val fullDayInformation = FullDayInformation(
                                    id = null,
                                    date = getDate(),
                                    currentTemp =it.temperature.toString(),
                                    currentFeelsLike = it.feltTemperature,
                                    currentCondition = it.description,
                                    currentWind = it.windSpeed,
                                    windDirection = it.windDirection,
                                    currentCity =  it.cityName,
                                    status = getStatusHot(),
                                    humidity = it.humidity,
                                    garb = saveListInDatabaseSearchVariant()
                                )
                                mainViewModel.insertFullDayInformation(fullDayInformation)
                            }
                        }
                    })
                }


            }
                binding.headCard.setOnClickListener {
                    DialogManager.showHeadDialogSearch(requireContext(), currentWeatherSearching)
                }
            }
        }
    }





    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseSearchVariant(): MutableList<WardrobeElement> {

        val resSave = mainViewModel.currentWeatherSearching.value?.temperature?.toInt()
        val conditionRainResponseSave = mainViewModel.currentWeatherSearching.value?.description.toString()
        val conditionRainListSave = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain"
        )

        if (resSave in -60..-35 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardCold)
            listWardrobeElement = list
        } else if (resSave in -60..-35 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardCold)
            listWardrobeElement = list
        } else if (resSave in -34..-27 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperCold)
            listWardrobeElement = list
        } else if (resSave in -34..-27 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperCold)
            listWardrobeElement = list
        } else if (resSave in -26..-15 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryCold)
            listWardrobeElement = list
        } else if (resSave in -26..-15 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryCold)
            listWardrobeElement = list
        } else if (resSave in -14..-5 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalCold)
            listWardrobeElement = list
        } else if (resSave in -14..-5 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalCold)
            listWardrobeElement = list
        } else if (resSave in -4..8 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitCold)
            listWardrobeElement = list
        } else if (resSave in -4..8 && conditionRainResponseSave in conditionRainListSave) {
            val list =
                mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitCold)
            listWardrobeElement = list
        } else if (resSave in 9..14 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitTransitHot)
            listWardrobeElement = list
        } else if (resSave in 9..14 && conditionRainResponseSave in conditionRainListSave) {
            val list =
                mainViewModel.getListWardrobeElements(rainClothesKit.kitRainTransitHot)
            listWardrobeElement = list
        } else if (resSave in 15..18 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitNormalHot)
            listWardrobeElement = list
        } else if (resSave in 15..18 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainNormalHot)
            listWardrobeElement = list
        } else if (resSave in 19..24 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitVeryHot)
            listWardrobeElement = list
        } else if (resSave in 19..24 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainVeryHot)
            listWardrobeElement = list
        } else if (resSave in 25..30 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitSuperHot)
            listWardrobeElement = list
        } else if (resSave in 25..30 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainSuperHot)
            listWardrobeElement = list
        } else if (resSave in 31..55 && conditionRainResponseSave !in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitHardHot)
            listWardrobeElement = list
        } else if (resSave in 31..55 && conditionRainResponseSave in conditionRainListSave) {
            val list = mainViewModel.getListWardrobeElements(rainClothesKit.kitRainHardHot)
            listWardrobeElement = list
        } else {
            val list = mainViewModel.getListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
            listWardrobeElement = list
        }

        return listWardrobeElement!!
    }













    //RecyclerView
    private fun initRcViewGarb() = with(binding) {
        rcViewGarb.layoutManager = LinearLayoutManager(activity)
        weatherAdapter = WeatherAdapter(this@WeatherFragment)
        rcViewGarb.adapter = weatherAdapter
    }

    private fun showDataInRcViewOnScreenObserver() {
        mainViewModel.wardrobeElementLists.observe(viewLifecycleOwner) {
            weatherAdapter.submitList(it)
        }
    }


    //Функции для получения GPS и местоположения координат
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
                    getMainWeatherForecast(location.latitude, location.longitude)
                } else {
                    val latitude = 58.0373
                    val longitude = 56.0381
                    getMainWeatherForecast(latitude, longitude)
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

    private fun windDegForm(currentWindDeg: Double): String {
        var statusWind: String? = null
        if(currentWindDeg in 348.75 ..361.00 || currentWindDeg in 0.00 .. 11.25 ) {
            statusWind = "North"
        } else if(currentWindDeg in 11.26 .. 56.25) {
            statusWind = "North/East"
        } else if(currentWindDeg in 56.26 .. 123.74) {
            statusWind = "East"
        } else if(currentWindDeg in 123.75 .. 168.74) {
            statusWind = "South/East"
        } else if(currentWindDeg in 168.75 .. 213.74) {
            statusWind = "South"
        } else if(currentWindDeg in 213.75 .. 258.74 ) {
            statusWind = "South/West"
        } else if(currentWindDeg in 258.75 .. 303.74) {
            statusWind = "West"
        } else if(currentWindDeg in 303.75 .. 348.74){
            statusWind = "North/West"
        } else statusWind = "Sorry, wind direction not found"
        return statusWind.toString()
    }


    //Инстанция фргамента
    companion object {
        @JvmStatic
        fun newInstance() = WeatherFragment()
    }
}









