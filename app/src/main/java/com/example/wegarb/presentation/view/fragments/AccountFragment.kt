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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.StringRequest
import com.example.wegarb.R
import com.example.wegarb.data.arrays.ArraysGarb
import com.example.wegarb.data.arrays.ArraysGarbRain
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.data.models.SearchWeatherModel
import com.example.wegarb.data.models.WeatherModel
import com.example.wegarb.data.models.WeatherModelCityName
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
    private val mainViewModel: MainViewModel by activityViewModels{
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




    private fun requestMainHeadCard(latitude: String, longitude: String) {
        val url =
            "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=&appid=$API_KEY"

        val queue = Volley.newRequestQueue(context)

        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> getMainResponseInJsonFormat(response) },
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

        val currentCityHeadRequestLatitude = responseJson.getString("lat")
        val currentCityHeadRequestLongitude = responseJson.getString("lon")
        val currentCityHead = "$currentCityHeadRequestLatitude / $currentCityHeadRequestLongitude"

        val currentConditionHeadRequest =
            responseJson.getJSONObject("current").getJSONArray("weather")
        val currentConditionHeadRequestObject = currentConditionHeadRequest[0] as JSONObject
        val currentConditionHead = currentConditionHeadRequestObject.getString("main")


        val headCardModel = WeatherModel(
            currentDataHead,
            currentTemperatureHead.toDouble().toInt().toString(),
            currentWindHead,
            currentCityHead,
            currentConditionHead
        )
        mainViewModel.mutableHeadCardWeatherModel.value = headCardModel
    }



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
    private fun showDataHeadCardOnScreenObserver() = with(binding) {

        mainViewModel.mutableHeadCardWeatherModel.observe(viewLifecycleOwner) {
            tvCurrentData.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentData.toString()
            tvCurrentTemperature.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature.toString()}°C"
            tvCurrentWind.text =
                "${mainViewModel.mutableHeadCardWeatherModel.value?.currentWind.toString()} m/c"
            tvCurrentCoordinate.text =
                "- lat/lon: ${mainViewModel.mutableHeadCardWeatherModel.value?.currentCoordinate.toString()}"
            tvCurrentCondition.text =
                mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            tvCityName.text =
                mainViewModel.mutableHeadCardWeatherModelCity.value?.currentCityName.toString()


            val res = mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature?.toInt()
            val conditionRainResponse = mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            val conditionRainList = mutableListOf("Rain", "rain","light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")



            if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
            } else if(res in -60..-35 && conditionRainResponse in conditionRainList){
            mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
            } else if(res in -34..-27 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
            } else if(res in -26..-15 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
            } else if(res in -14..-5 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
            } else if(res in -4..8 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
            } else if(res in 9..14 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
            } else if(res in 15..18 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
            } else if(res in 19..24 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
            } else if(res in 25..30 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
            } else if(res in 31..55 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
            } else {
                mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
            }
        }
    }

    fun requestForSearch(cityName: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$API_KEY"

        val queue = Volley.newRequestQueue(context)

        val mainRequestSearch = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response -> getSearchResponse(response) },
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

        val searchModelHead = SearchWeatherModel(
            currentDataHead,
            currentTemperatureHead.toInt(),
            currentWindHead.toString(),
            currentConditionHead,
            currentCityNameHead,
            currentCoordinateHead
        )
        mainViewModel.mutableHeadCardSearchModel.value = searchModelHead
    }

    fun showDataHeadCardOnScreenObserverSearch() = with(binding) {
        mainViewModel.mutableHeadCardSearchModel.observe(viewLifecycleOwner) {
            tvCurrentData.text = mainViewModel.mutableHeadCardSearchModel.value?.currentData.toString()
            tvCurrentTemperature.text = "${mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature.toString()}°C"
            tvCurrentWind.text = "${mainViewModel.mutableHeadCardSearchModel.value?.currentWind.toString()} m/c"
            tvCurrentCondition.text = mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            tvCityName.text = mainViewModel.mutableHeadCardSearchModel.value?.currentCityName.toString()
            tvCurrentCoordinate.text = "- lat/lon: ${mainViewModel.mutableHeadCardSearchModel.value?.currentCoordinate.toString()}"


            val res = mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature
            val conditionRainResponse = mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            val conditionRainList = mutableListOf("Rain", "rain", "light rain", "moderate rain", "heavy intensity rain", "very heavy rain", "extreme rain", "freezing rain", "light intensity shower rain", "shower rain", "heavy intensity shower rain", "ragged shower rain")

            if (res in -60..-35 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
            } else if(res in -60..-35 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbHardColdRain)
            } else if (res in -34..-27 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
            } else if(res in -34..-27 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbSuperColdRain)
            } else if (res in -26..-15 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
            } else if(res in -26..-15 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbColdRain)
            } else if (res in -14..-5 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
            } else if(res in -14..-5 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalColdRain)
            } else if (res in -4..8 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
            } else if(res in -4..8 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionColdRain)
            } else if (res in 9..14 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
            } else if(res in 9..14 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbTransitionHotRain)
            } else if (res in 15..18 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
            } else if(res in 15..18 && conditionRainResponse in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarbRain.mListNameGarbNormalHotRain)
            } else if (res in 19..24 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
            } else if(res in 19..24 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHotRain)
            } else if (res in 25..30 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
            } else if(res in 25..30 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothSuperHotRain)
            } else if (res in 31..55 && conditionRainResponse !in conditionRainList) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
            } else if(res in 31..55 && conditionRainResponse in conditionRainList){
                mainViewModel.setMyModelList(arraysGarbRain.mListNameClothHardHotRain)
            } else {
                mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
            }
        }
    }


    /*
   The next 6 function have the following responsibility:
   - We checked - Torn on GPS permission on phone user or no.
   - We checked - Give me "permission location" user or no.
   - And finally, we get location user coordinate. and show her on Screen in HeadCardView
    */
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


    /*
    The next 2 functions have the following responsibility:
    - Initialization "RecyclerView" and assignment her "Adapter"
    - Connect Observer, her observe update data.
    - And finally, show data on screen.
    */
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






    private fun saveInfoModelInDatabaseHead() {
        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.mutableHeadCardWeatherModel.observe(lifecycleOwner) {
            val cTemp = mainViewModel.mutableHeadCardWeatherModel.value?.currentTemperature.toString()
            val cCond = mainViewModel.mutableHeadCardWeatherModel.value?.currentCondition.toString()
            val cWind = mainViewModel.mutableHeadCardWeatherModel.value?.currentWind.toString()
            val cCity = mainViewModel.mutableHeadCardWeatherModelCity.value?.currentCityName.toString()
        binding.buttonSaveState.setOnClickListener {


            DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick() {


                    val infoModel = InfoModel(
                        id = null,
                        date = getDate(),
                        currentTemp = cTemp,
                        currentCondition = cCond,
                        currentWind = cWind,
                        currentCity = cCity
                    )

                    mainViewModel.insertInfoModelInDataBase(infoModel)
                }

            })
        }
        }
    }
    private fun saveInfoModelInDatabaseSearch() {
        val lifecycleOwner = viewLifecycleOwner
        mainViewModel.mutableHeadCardSearchModel.observe(lifecycleOwner) {
            val cTemp = mainViewModel.mutableHeadCardSearchModel.value?.currentTemperature.toString()
            val cCond = mainViewModel.mutableHeadCardSearchModel.value?.currentCondition.toString()
            val cWind = mainViewModel.mutableHeadCardSearchModel.value?.currentWind.toString()
            val cCity = mainViewModel.mutableHeadCardSearchModel.value?.currentCityName.toString()
        binding.buttonSaveState.setOnClickListener {


            DialogManager.showSaveDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick() {


                    val infoModel = InfoModel(
                        id = null,
                        date = getDate(),
                        currentTemp = cTemp,
                        currentCondition = cCond,
                        currentWind = cWind,
                        currentCity = cCity
                    )

                    mainViewModel.insertInfoModelInDataBase(infoModel)
                }

            })
        }
        }
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

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }

    override fun onClickItem(garbModel: GarbModel) {
        DialogManager.showClothDialog(requireContext(), garbModel)
    }

}

