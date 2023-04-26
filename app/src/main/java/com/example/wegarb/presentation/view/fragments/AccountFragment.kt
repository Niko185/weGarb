package com.example.wegarb.presentation.view.fragments
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
import com.example.wegarb.data.arrays.ArraysGarb
import com.example.wegarb.data.arrays.ArraysGarbRain
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.data.models.*
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.presentation.view.adapters.GarbAdapter
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

class AccountFragment : Fragment(), GarbAdapter.Listener {
    private lateinit var binding: FragmentAccountBinding
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var garbAdapter: GarbAdapter
    private val arraysGarb: ArraysGarb = ArraysGarb()
    private val arraysGarbRain: ArraysGarbRain = ArraysGarbRain()
    private var listGarbModel: MutableList<GarbModel>? = null
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
        val url =
            "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=&appid=$API_KEY"

        val queue = Volley.newRequestQueue(context)

        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                getMainResponseInJsonFormat(response)
                Log.d("MyLog", "rrrrrr - $response")
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

        val headModel = HeadModel(
            currentTemp = currentTemperatureHead.toDouble().toInt().toString(),
            cFellsLike = currentFeelsLike.toDouble().toInt().toString(),
            wind = currentWindHead,
            windVariant = resultWindDeg,
            humidity = currentHumidity,
        )
        mainViewModel.mutableHeadModel.value = headModel

        binding.headCard.setOnClickListener {
            DialogManager.showHeadDialog(requireContext(), headModel)
        }

        val headCardModel = WeatherModel(
            currentDataHead,
            currentTemperatureHead.toDouble().toInt().toString(),
            currentWindHead,
            currentCityHead,
            currentConditionHead
        )
        mainViewModel.mutableHeadCardWeatherModel.value = headCardModel
    }


    // Подтягиваем название города по координатам
    internal fun requestApiCityName(latitude: String, longitude: String) {
        val url =
            "https://api.openweathermap.org/geo/1.0/reverse?lat=$latitude&lon=$longitude&limit=1&appid=$API_KEY"
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

        val cityNameModel = WeatherModelCityName(
            currentNameCityHeadRequest
        )
        mainViewModel.mutableHeadCardWeatherModelCity.value = cityNameModel
    }


    @SuppressLint("SetTextI18n")
    private fun showDataHeadCardOnScreenObserver() {

        //HeadCard Отображение
        mainViewModel.mutableHeadCardWeatherModel.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentWind.toString()} m/c"
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mutableHeadCardWeatherModel.value?.currentCoordinate.toString()}"
            binding.tvCurrentCondition.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.mutableHeadCardWeatherModelCity.value?.currentCityName.toString()


            //RecyclerView Отображение
            val res = mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature?.toInt()
            val conditionRainResponse =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
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
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
                listGarbModel = list
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
                listGarbModel = list
            } else {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
                listGarbModel = list
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseCoordinateVariant(): MutableList<GarbModel> {

        mainViewModel.mutableHeadCardWeatherModel.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentWind.toString()} m/c"
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mutableHeadCardWeatherModel.value?.currentCoordinate.toString()}"
            binding.tvCurrentCondition.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.mutableHeadCardWeatherModelCity.value?.currentCityName.toString()


            val res = mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature?.toInt()
            val conditionRainResponse =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
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
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
                listGarbModel = list
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                val list = mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
                listGarbModel = list
            } else {
                val list = mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
                listGarbModel = list
            }
        }
        return listGarbModel!!
    }


    // Сохранение данных в Info Model и cохранение заполненной InfoModel в Хранилище для КООРДИНАТ
    private fun saveInfoModelInDatabaseHead() {
        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.mutableHeadCardWeatherModel.observe(lifecycleOwner) {
            val cTemp =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature.toString()
            val cCond = mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            val cWind = mainViewModel.mutableHeadCardWeatherModel.value?.currentWind.toString()
            val cCity =
                mainViewModel.mutableHeadCardWeatherModelCity.value?.currentCityName.toString()

            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.mutableHeadModel.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.mutableHeadModel.value?.cFellsLike.toString()
                val windDir = mainViewModel.mutableHeadModel.value?.windVariant.toString()
                val humidity = mainViewModel.mutableHeadModel.value?.humidity.toString()


                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {
                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
                        }

                        override fun onClickCold() {
                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
                        }

                        override fun onClickHot() {
                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
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
                Log.d("MyLog", "maaaaiiinnn - $response")
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

        val searchModelHead = SearchWeatherModel(
            currentDataHead,
            currentTemperatureHead.toInt(),
            currentWindHead.toString(),
            currentConditionHead,
            currentCityNameHead,
            currentCoordinateHead
        )
        mainViewModel.mutableHeadCardSearchModel.value = searchModelHead

        val headModelSearch = HeadModel(
            currentTemp = currentTemperatureHead.toDouble().toInt().toString(),
            cFellsLike = currentFeelsLike.toDouble().toInt().toString(),
            wind = currentWindHead.toString(),
            windVariant = resultWindDeg,
            humidity = currentHumidity
        )
        mainViewModel.mutableHeadModel.value = headModelSearch

        binding.headCard.setOnClickListener {
            DialogManager.showHeadDialog(requireContext(), headModelSearch)
        }
    }


    fun showDataHeadCardOnScreenObserverSearch() = with(binding) {

        //HeadCard Отображение
        mainViewModel.mutableHeadCardSearchModel.observe(viewLifecycleOwner) {
            tvCurrentData.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentData.toString()
            tvCurrentTemperature.text =
                "${mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature.toString()}°C"
            tvCurrentWind.text =
                "${mainViewModel.mutableHeadCardSearchModel.value?.currentWind.toString()} m/c"
            tvCurrentCondition.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            tvCityName.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCityName.toString()
            tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mutableHeadCardSearchModel.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature
            val conditionRainResponse =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
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
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
            } else {
                mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun saveListInDatabaseSearchVariant(): MutableList<GarbModel> {

        mainViewModel.mutableHeadCardSearchModel.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentData.toString()
            binding.tvCurrentTemperature.text =
                "${mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature.toString()}°C"
            binding.tvCurrentWind.text =
                "${mainViewModel.mutableHeadCardSearchModel.value?.currentWind.toString()} m/c"
            binding.tvCurrentCondition.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            binding.tvCityName.text =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCityName.toString()
            binding.tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mutableHeadCardSearchModel.value?.currentCoordinate.toString()}"


            //RecyclerView Отображение
            val res = mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature
            val conditionRainResponse =
                mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
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
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
                listGarbModel = list
            } else if (res in -60..-35 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
                listGarbModel = list
            } else if (res in -34..-27 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
                listGarbModel = list
            } else if (res in -26..-15 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
                listGarbModel = list
            } else if (res in -14..-5 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
                listGarbModel = list
            } else if (res in -4..8 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
                listGarbModel = list
            } else if (res in 9..14 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
                listGarbModel = list
            } else if (res in 15..18 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
                listGarbModel = list
            } else if (res in 19..24 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
                listGarbModel = list
            } else if (res in 25..30 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
                listGarbModel = list
            } else if (res in 31..55 && conditionRainResponse in conditionRainList) {
                val list =
                    mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
                listGarbModel = list
            } else {
                val list =
                    mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
                listGarbModel = list
            }
        }
        return listGarbModel!!
    }


    // Сохранение данных в Хранилище для ПОИСКА ПО НАЗВАНИЮ ГОРОДА
    private fun saveInfoModelInDatabaseSearch() {

        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.mutableHeadCardSearchModel.observe(lifecycleOwner) {
            val cTemp =
                mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature.toString()
            val cCond = mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            val cWind = mainViewModel.mutableHeadCardSearchModel.value?.currentWind.toString()
            val cCity = mainViewModel.mutableHeadCardSearchModel.value?.currentCityName.toString()

            val lifecycleOwnerHead = viewLifecycleOwner
            mainViewModel.mutableHeadModel.observe(lifecycleOwnerHead) {
                val cFeelsLike = mainViewModel.mutableHeadModel.value?.cFellsLike.toString()
                val windDir = mainViewModel.mutableHeadModel.value?.windVariant.toString()
                val humidity = mainViewModel.mutableHeadModel.value?.humidity.toString()


                binding.buttonSaveState.setOnClickListener {
                    DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {

                        override fun onClickComfort() {

                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
                        }

                        override fun onClickCold() {
                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
                        }

                        override fun onClickHot() {
                            val infoModel = InfoModel(
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
                            mainViewModel.insertInfoModelInDataBase(infoModel)
                        }
                    })
                }
            }
        }
    }





    //RecyclerView
    private fun initRcViewGarb() = with(binding) {
        rcViewGarb.layoutManager = LinearLayoutManager(activity)
        garbAdapter = GarbAdapter(this@AccountFragment)
        rcViewGarb.adapter = garbAdapter
    }

    private fun showDataInRcViewOnScreenObserver() {
        mainViewModel.mutableRcViewGarbModel.observe(viewLifecycleOwner) {
            garbAdapter.submitList(it)
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
    override fun onClickItem(garbModel: GarbModel) {
        DialogManager.showClothDialog(requireContext(), garbModel)
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
        fun newInstance() = AccountFragment()
    }
}








