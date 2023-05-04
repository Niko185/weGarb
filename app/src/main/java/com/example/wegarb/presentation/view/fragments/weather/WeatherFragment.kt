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
import com.example.wegarb.domain.models.old.*
import com.example.wegarb.domain.models.show.CurrentWeather
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

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"

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
        /*saveInfoModelInDatabaseHead()*/

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
                mainViewModel.currentWeather.observe(viewLifecycleOwner){
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
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitHardCold)
                        listWardrobeElement = list
                    } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperCold)
                        listWardrobeElement = list
                    } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryCold)
                        listWardrobeElement = list
                    } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalCold)
                        listWardrobeElement = list
                    } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitCold)
                        listWardrobeElement = list
                    } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                        val list =
                            mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitCold)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitHot)
                        listWardrobeElement = list
                    } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                        val list =
                            mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalHot)
                        listWardrobeElement = list
                    } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryHot)
                        listWardrobeElement = list
                    } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperHot)
                        listWardrobeElement = list
                    } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitHardHot)
                        listWardrobeElement = list
                    } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                        val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardHot)
                        listWardrobeElement = list
                    } else {
                        val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
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
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitHardCold)
                listWardrobeElement = list
            } else if (resSave in -60..-35 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperCold)
                listWardrobeElement = list
            } else if (resSave in -34..-27 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryCold)
                listWardrobeElement = list
            } else if (resSave in -26..-15 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalCold)
                listWardrobeElement = list
            } else if (resSave in -14..-5 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitCold)
                listWardrobeElement = list
            } else if (resSave in -4..8 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitCold)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitHot)
                listWardrobeElement = list
            } else if (resSave in 9..14 && conditionRainResponseSave in conditionRainListSave) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalHot)
                listWardrobeElement = list
            } else if (resSave in 15..18 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryHot)
                listWardrobeElement = list
            } else if (resSave in 19..24 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperHot)
                listWardrobeElement = list
            } else if (resSave in 25..30 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave !in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitHardHot)
                listWardrobeElement = list
            } else if (resSave in 31..55 && conditionRainResponseSave in conditionRainListSave) {
                val list = mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardHot)
                listWardrobeElement = list
            } else {
                val list = mainViewModel.setListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
                listWardrobeElement = list
            }

        return listWardrobeElement!!
    }


    /*// Сохранение данных в FullDayInformation и cохранение заполненной FullDayInformation в Хранилище для КООРДИНАТ
    private fun saveInfoModelInDatabaseHead() {
        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.currentWeather.observe(lifecycleOwner) {
            val cTemp = mainViewModel.currentWeather.value?.temperature.toString()
            val cCond = mainViewModel.currentWeather.value?.description.toString()
            val cWind = mainViewModel.currentWeather.value?.windSpeed.toString()


            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.additionalWeatherForecast.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.additionalWeatherForecast.value?.feltTemperature.toString()
                val windDir = mainViewModel.additionalWeatherForecast.value?.windDirection.toString()
                val humidity = mainViewModel.additionalWeatherForecast.value?.humidity.toString()


                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {
                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = "",
                                status = getStatusComfort(),
                                humidity = humidity,
                                garb = saveListInDatabaseCoordinateVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }

                        override fun onClickCold() {
                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = "",
                                status = getStatusCold(),
                                humidity = humidity,
                                garb = saveListInDatabaseCoordinateVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }

                        override fun onClickHot() {
                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = "",
                                status = getStatusHot(),
                                humidity = humidity,
                                garb = saveListInDatabaseCoordinateVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }
                    })
                }
            }
        }
    }*/













   /* // Запрос по названию города(через поиск в приложении)
    fun requestForSearch(cityName: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY"

        val queue = Volley.newRequestQueue(context)

        val mainRequestSearch = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                getSearchResponse(response)
            },
            { error -> Log.d("Mylog", "error: $error") }
        )

        queue.add(mainRequestSearch)
    }*/

   /* private fun getSearchResponse(response: JSONObject) {

        val weather = response.getJSONArray("weather").getJSONObject(0)

        val coordinates = response.getJSONObject("coord")
        val latitude = coordinates.getDouble("lat")
        val longitude = coordinates.getDouble("lon")
        val currentCoordinateHead = "$latitude / $longitude"


        val currentDataHeadUnix = response.getLong("dt").toString()
        val currentDataHead = formatterUnix(currentDataHeadUnix)

        val currentWindHead = response.getJSONObject("wind").getDouble("speed").toInt()
        val currentConditionHead = weather.getString("description")
        val currentCityNameHead = response.getString("name")

        val temperatureInKelvin = response.getJSONObject("main").getDouble("temp")
        val currentTemperatureHead = temperatureInKelvin - 273.15 // convert to Celsius

        val currentFeelsLikeInKelvin =
            response.getJSONObject("main").getString("feels_like").toDouble()
        val currentFeelsLike = currentFeelsLikeInKelvin - 273.15

        val currentWindDeg = response.getJSONObject("wind").getString("deg").toDouble()
        val resultWindDeg = windDegForm(currentWindDeg)

        val currentHumidity = response.getJSONObject("main").getString("humidity")

        val searchModelHead = SearchingWeatherForecast(
            currentDataHead,
            currentTemperatureHead.toInt(),
            currentWindHead.toString(),
            currentConditionHead,
            currentCityNameHead,
            currentCoordinateHead
        )
        mainViewModel.searchingWeatherForecast.value = searchModelHead

        val additionalWeatherForecastSearch = AdditionalWeatherForecast(
            currentTemperature = currentTemperatureHead.toDouble().toInt().toString(),
            feltTemperature = currentFeelsLike.toDouble().toInt().toString(),
            wind = currentWindHead.toString(),
            windDirection = resultWindDeg,
            humidity = currentHumidity
        )
        mainViewModel.additionalWeatherForecast.value = additionalWeatherForecastSearch

        binding.headCard.setOnClickListener {
            DialogManager.showHeadDialog(requireContext(), additionalWeatherForecastSearch)
        }
    }


    fun showDataHeadCardOnScreenObserverSearch() = with(binding) {

        //HeadCard Отображение
        mainViewModel.searchingWeatherForecast.observe(viewLifecycleOwner) {
            tvCurrentData.text =
                mainViewModel.searchingWeatherForecast.value?.date.toString()
            tvCurrentTemperature.text =
                "${mainViewModel.searchingWeatherForecast.value?.temperature.toString()}°C"
            tvCurrentWind.text =
                "${mainViewModel.searchingWeatherForecast.value?.windSpeed.toString()} m/c"
            tvCurrentCondition.text =
                mainViewModel.searchingWeatherForecast.value?.description.toString()
            tvCityName.text =
                mainViewModel.searchingWeatherForecast.value?.city.toString()
            tvCurrentCoordinate.text = "- lat/lon: ${mainViewModel.searchingWeatherForecast.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.searchingWeatherForecast.value?.temperature
            val conditionRainResponse =
                mainViewModel.searchingWeatherForecast.value?.description.toString()
            val conditionRainList = mutableListOf(
                "Rain",
                "rain",
                "light rain",
                "moderate rain",
                "heavy intensity rain",
                "very heavy rain",
                "extreme rain",
                "freezing rain",
                "light intensity shower rain",
                "shower rain",
                "heavy intensity shower rain",
                "ragged shower rain"
            )

            if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitHardCold)
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardCold)
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperCold)
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperCold)
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryCold)
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryCold)
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalCold)
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalCold)
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitCold)
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitCold)
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitHot)
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitHot)
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalHot)
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalHot)
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryHot)
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryHot)
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperHot)
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperHot)
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitHardHot)
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardHot)
            } else {
                mainViewModel.setListWardrobeElements(baseClothesKit.kitAllElementsWardrobe)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseSearchVariant(): MutableList<WardrobeElement> {

        mainViewModel.searchingWeatherForecast.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.searchingWeatherForecast.value?.date.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.searchingWeatherForecast.value?.temperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.searchingWeatherForecast.value?.windSpeed.toString()} m/c"
            binding.tvCurrentCondition.text =
                mainViewModel.searchingWeatherForecast.value?.description.toString()
            binding.tvCityName.text =
                mainViewModel.searchingWeatherForecast.value?.city.toString()
            binding.tvCurrentCoordinate.text = "- lat/lon: ${mainViewModel.searchingWeatherForecast.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.searchingWeatherForecast.value?.temperature
            val conditionRainResponse =
                mainViewModel.searchingWeatherForecast.value?.description.toString()
            val conditionRainList = mutableListOf(
                "Rain",
                "rain",
                "light rain",
                "moderate rain",
                "heavy intensity rain",
                "very heavy rain",
                "extreme rain",
                "freezing rain",
                "light intensity shower rain",
                "shower rain",
                "heavy intensity shower rain",
                "ragged shower rain"
            )

            if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitHardCold)
                listWardrobeElement = list
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardCold)
                listWardrobeElement = list
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperCold)
                listWardrobeElement = list
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperCold)
                listWardrobeElement = list
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryCold)
                listWardrobeElement = list
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryCold)
                listWardrobeElement = list
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalCold)
                listWardrobeElement = list
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalCold)
                listWardrobeElement = list
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitCold)
                listWardrobeElement = list
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitCold)
                listWardrobeElement = list
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitTransitHot)
                listWardrobeElement = list
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainTransitHot)
                listWardrobeElement = list
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitNormalHot)
                listWardrobeElement = list
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainNormalHot)
                listWardrobeElement = list
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitVeryHot)
                listWardrobeElement = list
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainVeryHot)
                listWardrobeElement = list
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitSuperHot)
                listWardrobeElement = list
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainSuperHot)
                listWardrobeElement = list
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(baseClothesKit.kitHardHot)
                listWardrobeElement = list
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainHardHot)
                listWardrobeElement = list
            } else {
                val list =
                    mainViewModel.setListWardrobeElements(rainClothesKit.kitRainAllElementsWardrobe)
                listWardrobeElement = list
            }
        }
        return listWardrobeElement!!
    }


    // Сохранение данных в Хранилище для ПОИСКА ПО НАЗВАНИЮ ГОРОДА
    private fun saveInfoModelInDatabaseSearch() {

        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.searchingWeatherForecast.observe(lifecycleOwner) {
            val cTemp =
                mainViewModel.searchingWeatherForecast.value?.temperature.toString()
            val cCond = mainViewModel.searchingWeatherForecast.value?.description.toString()
            val cWind = mainViewModel.searchingWeatherForecast.value?.windSpeed.toString()
            val cCity = mainViewModel.searchingWeatherForecast.value?.city.toString()

            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.additionalWeatherForecast.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.additionalWeatherForecast.value?.feltTemperature.toString()
                val windDir = mainViewModel.additionalWeatherForecast.value?.windDirection.toString()
                val humidity = mainViewModel.additionalWeatherForecast.value?.humidity.toString()


                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {

                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = cCity,
                                status = getStatusComfort(),
                                humidity = humidity,
                                garb = saveListInDatabaseSearchVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }

                        override fun onClickCold() {
                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = cCity,
                                status = getStatusCold(),
                                humidity = humidity,
                                garb = saveListInDatabaseSearchVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }

                        override fun onClickHot() {
                            val fullDayInformation = FullDayInformation(
                                id = null,
                                date = getDate(),
                                currentTemp = cTemp,
                                currentFeelsLike = cFeelsLike,
                                currentCondition = cCond,
                                currentWind = cWind,
                                windDirection = windDir,
                                currentCity = cCity,
                                status = getStatusHot(),
                                humidity = humidity,
                                garb = saveListInDatabaseSearchVariant()
                            )
                            mainViewModel.insertFullDayInformation(fullDayInformation)
                        }
                    })
                }
            }
        }
    }*/





    //RecyclerView
    private fun initRcViewGarb() = with(binding) {
        rcViewGarb.layoutManager = LinearLayoutManager(activity)
        weatherAdapter = WeatherAdapter(this@WeatherFragment)
        rcViewGarb.adapter = weatherAdapter
    }

    private fun showDataInRcViewOnScreenObserver() {
        mainViewModel.wardrobeElement.observe(viewLifecycleOwner) {
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



               /* requestMainHeadCard("${it.result.latitude}", "${it.result.longitude}")
                requestApiCityName("${it.result.latitude}", "${it.result.longitude}")*/
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









