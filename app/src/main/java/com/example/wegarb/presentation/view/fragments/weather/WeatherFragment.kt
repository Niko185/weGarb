package com.example.wegarb.presentation.view.fragments.weather
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.example.wegarb.data.arrays.BaseClothesKit
import com.example.wegarb.data.arrays.RainClothesKit
import com.example.wegarb.data.database.entity.FullDayInformation
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.domain.models.*
import com.example.wegarb.presentation.vm.MainViewModel
import com.example.wegarb.utils.DialogManager
import com.example.wegarb.utils.GpsDialog
import com.example.wegarb.utils.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"

class WeatherFragment : Fragment(), WeatherAdapter.Listener {
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
        showDataHeadCardOnScreenObserver()
        initRcViewGarb()
        showDataInRcViewOnScreenObserver()
        saveInfoModelInDatabaseHead()
        saveInfoModelInDatabaseSearch()
    }

    override fun onResume() {
        super.onResume()
        getMyLocationNow()
    }


    // Запрос по координатам
    private fun requestMainHeadCard(latitude: String, longitude: String) {
        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=&appid=$API_KEY"

        val queue = Volley.newRequestQueue(context)

        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                getMainResponseInJsonFormat(response)
            },

            { error -> Log.d("Mylog", "error: $error") }
        )

        queue.add(mainRequest)
    }

    private fun getMainResponseInJsonFormat(response: String) {
        val responseJson = JSONObject(response)
        parsingMainHeadCard(responseJson)
    }

    private fun parsingMainHeadCard(responseJson: JSONObject) {

        val currentDataHeadRequest = responseJson.getJSONObject("current").getString("dt")
        val currentDataHead = formatterUnix(currentDataHeadRequest)

        val currentTemperatureHead = responseJson.getJSONObject("current").getString("temp")

        val currentWindHead = responseJson.getJSONObject("current").getString("wind_speed")
        val currentWindHeadDeg =
            responseJson.getJSONObject("current").getString("wind_deg").toDouble()
        var resultWindDeg = windDegForm(currentWindHeadDeg)

        val currentCityHeadRequestLatitude = responseJson.getString("lat")
        val currentCityHeadRequestLongitude = responseJson.getString("lon")
        val currentCityHead = "$currentCityHeadRequestLatitude / $currentCityHeadRequestLongitude"

        val currentConditionHeadRequest =
            responseJson.getJSONObject("current").getJSONArray("weather")
        val currentConditionHeadRequestObject = currentConditionHeadRequest[0] as JSONObject
        val currentConditionHead = currentConditionHeadRequestObject.getString("main")

        val currentHumidity = responseJson.getJSONObject("current").getString("humidity")
        val currentFeelsLike = responseJson.getJSONObject("current").getString("feels_like")

        val additionalWeatherForecast = AdditionalWeatherForecast(
            currentTemp = currentTemperatureHead.toDouble().toInt().toString(),
            cFellsLike = currentFeelsLike.toDouble().toInt().toString(),
            wind = currentWindHead,
            windVariant = resultWindDeg,
            humidity = currentHumidity,
        )
        mainViewModel.additionalWeatherForecast.value = additionalWeatherForecast

        binding.headCard.setOnClickListener {
            DialogManager.showHeadDialog(requireContext(), additionalWeatherForecast)
        }

        val headCardModel = MainWeatherForecast(
            currentDataHead,
            currentTemperatureHead.toDouble().toInt().toString(),
            currentWindHead,
            currentCityHead,
            currentConditionHead
        )
        mainViewModel.mainWeatherForecast.value = headCardModel
    }


    // Подтягиваем название города по координатам
    private fun requestApiCityName(latitude: String, longitude: String) {
        val url = "https://api.openweathermap.org/geo/1.0/reverse?lat=$latitude&lon=$longitude&limit=1&appid=$API_KEY"
        val queue = Volley.newRequestQueue(context)


        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { responseCity -> getCityResponse(responseCity) },
            { error -> Log.d("Mylog", "error: $error") }
        )
        queue.add(mainRequest)
    }

    private fun getCityResponse(responseCity: String) {
        val responseJsonCity = JSONArray(responseCity)
        parsingApiCity(responseJsonCity)
    }

    private fun parsingApiCity(responseJsonCity: JSONArray) {

        val currentNameCityHeadRequest = responseJsonCity
            .getJSONObject(0)
            .getJSONObject("local_names")
            .getString("es")

        val cityNameModel = CurrentCity(currentNameCityHeadRequest)
        mainViewModel.currentCity.value = cityNameModel
    }


    @SuppressLint("SetTextI18n")
    private fun showDataHeadCardOnScreenObserver() {

        //HeadCard Отображение
        mainViewModel.mainWeatherForecast.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.mainWeatherForecast.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.mainWeatherForecast.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.mainWeatherForecast.value?.currentWind.toString()} m/c"
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mainWeatherForecast.value?.currentCoordinate.toString()}"
            binding.tvCurrentCondition.text =
                mainViewModel.mainWeatherForecast.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.currentCity.value?.currentCityName.toString()


            //Логика выдачи списка в RecyclerView
            val res = mainViewModel.mainWeatherForecast.value?.currentTemperature?.toInt()
            val conditionRainResponse = mainViewModel.mainWeatherForecast.value?.currentCondition.toString()
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
        }
    }

    //Логика сохранения списка из RecyclerView
    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseCoordinateVariant(): MutableList<WardrobeElement> {

        mainViewModel.mainWeatherForecast.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.mainWeatherForecast.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.mainWeatherForecast.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.mainWeatherForecast.value?.currentWind.toString()} m/c"
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mainWeatherForecast.value?.currentCoordinate.toString()}"
            binding.tvCurrentCondition.text =
                mainViewModel.mainWeatherForecast.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.currentCity.value?.currentCityName.toString()


            val res = mainViewModel.mainWeatherForecast.value?.currentTemperature?.toInt()
            val conditionRainResponse =
                mainViewModel.mainWeatherForecast.value?.currentCondition.toString()
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
        }
        return listWardrobeElement!!
    }


    // Сохранение данных в Info Model и cохранение заполненной InfoModel в Хранилище для КООРДИНАТ
    private fun saveInfoModelInDatabaseHead() {
        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.mainWeatherForecast.observe(lifecycleOwner) {
            val cTemp = mainViewModel.mainWeatherForecast.value?.currentTemperature.toString()
            val cCond = mainViewModel.mainWeatherForecast.value?.currentCondition.toString()
            val cWind = mainViewModel.mainWeatherForecast.value?.currentWind.toString()
            val cCity = mainViewModel.currentCity.value?.currentCityName.toString()

            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.additionalWeatherForecast.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.additionalWeatherForecast.value?.cFellsLike.toString()
                val windDir = mainViewModel.additionalWeatherForecast.value?.windVariant.toString()
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
                                currentCity = cCity,
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
                                currentCity = cCity,
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
    }













    // Запрос по названию города(через поиск в приложении)
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
    }

    private fun getSearchResponse(response: JSONObject) {

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
            currentTemp = currentTemperatureHead.toDouble().toInt().toString(),
            cFellsLike = currentFeelsLike.toDouble().toInt().toString(),
            wind = currentWindHead.toString(),
            windVariant = resultWindDeg,
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
                mainViewModel.searchingWeatherForecast.value?.currentData.toString()
            tvCurrentTemperature.text =
                "${mainViewModel.searchingWeatherForecast.value?.currentTemperature.toString()}°C"
            tvCurrentWind.text =
                "${mainViewModel.searchingWeatherForecast.value?.currentWind.toString()} m/c"
            tvCurrentCondition.text =
                mainViewModel.searchingWeatherForecast.value?.currentCondition.toString()
            tvCityName.text =
                mainViewModel.searchingWeatherForecast.value?.currentCityName.toString()
            tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.searchingWeatherForecast.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.searchingWeatherForecast.value?.currentTemperature
            val conditionRainResponse =
                mainViewModel.searchingWeatherForecast.value?.currentCondition.toString()
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
                mainViewModel.searchingWeatherForecast.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.searchingWeatherForecast.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.searchingWeatherForecast.value?.currentWind.toString()} m/c"
            binding.tvCurrentCondition.text =
                mainViewModel.searchingWeatherForecast.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.searchingWeatherForecast.value?.currentCityName.toString()
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.searchingWeatherForecast.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.searchingWeatherForecast.value?.currentTemperature
            val conditionRainResponse =
                mainViewModel.searchingWeatherForecast.value?.currentCondition.toString()
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
                mainViewModel.searchingWeatherForecast.value?.currentTemperature.toString()
            val cCond = mainViewModel.searchingWeatherForecast.value?.currentCondition.toString()
            val cWind = mainViewModel.searchingWeatherForecast.value?.currentWind.toString()
            val cCity = mainViewModel.searchingWeatherForecast.value?.currentCityName.toString()

            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.additionalWeatherForecast.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.additionalWeatherForecast.value?.cFellsLike.toString()
                val windDir = mainViewModel.additionalWeatherForecast.value?.windVariant.toString()
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
    }





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
            .addOnCompleteListener {
                requestMainHeadCard("${it.result.latitude}", "${it.result.longitude}")
                requestApiCityName("${it.result.latitude}", "${it.result.longitude}")
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








