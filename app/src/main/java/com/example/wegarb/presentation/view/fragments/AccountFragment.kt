package com.example.wegarb.presentation.view.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wegarb.data.arrays.ArraysGarb
import com.example.wegarb.data.estimate.SearchEstimate
import com.example.wegarb.data.models.WeatherModel
import com.example.wegarb.data.models.WeatherModelCityName
import com.example.wegarb.data.requests.search.SearchRequest
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.presentation.view.adapters.GarbAdapter
import com.example.wegarb.presentation.vm.MainViewModel
import com.example.wegarb.utils.GpsDialog
import com.example.wegarb.utils.formatterUnix
import com.example.wegarb.utils.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONArray
import org.json.JSONObject

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var garbAdapter: GarbAdapter
    private val searchEstimate = SearchEstimate()
    private val searchRequest: SearchRequest = SearchRequest()
    private val arraysGarb: ArraysGarb = ArraysGarb()
    private val mainViewModel: MainViewModel by activityViewModels()


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

    }

    override fun onResume() {
        super.onResume()
        getMyLocationNow()
    }


    /*
    The next 8 functions have the following responsibility:
    - We get response from Web and extract specific data for show result.
    - Show extract data on screen in "HeadCardView".
    - As well we send extract data in "MainViewModel", use object DataClass "WeatherData"
    - And finally, we certain, appoint elements "HeadCardView" to results callback "MainViewModel&WeatherData"
     */
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
            if (res in -60..-35) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
            } else if (res in -34..-27) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
            } else if (res in -26..-15) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
            } else if (res in -14..-5) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
            } else if (res in -4..8) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
            } else if (res in 9..14) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
            } else if (res in 15..18) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
            } else if (res in 19..24) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
            } else if (res in 25..30) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
            } else if (res in 31..55) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
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
        garbAdapter = GarbAdapter()
        rcViewGarb.adapter = garbAdapter
    }

    private fun showDataInRcViewOnScreenObserver() {
    mainViewModel.mutableRcViewGarbModel.observe(viewLifecycleOwner) {
        garbAdapter.submitList(it)
    }
}

    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }


    fun requestForSearch(cityName: String) {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=${com.example.wegarb.data.requests.search.API_KEY}"

        val queue = Volley.newRequestQueue(context)

        val mainRequestSearch = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                searchRequest.getSearchResponse(response)
                mainViewModel.mutableHeadCardSearchModel.value = searchRequest.getSearchResponse(response)
                Log.d("Mylog", "responce: $response")
            },
            { error -> Log.d("Mylog", "error: $error") }
        )
        queue.add(mainRequestSearch)

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
            if (res in -60..-35) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbHardCold)
            } else if (res in -34..-27) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbSuperCold)
            } else if (res in -26..-15) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbCold)
            } else if (res in -14..-5) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalCold)
            } else if (res in -4..8) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionCold)
            } else if (res in 9..14) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbTransitionHot)
            } else if (res in 15..18) {
                mainViewModel.setMyModelList(arraysGarb.mListNameGarbNormalHot)
            } else if (res in 19..24) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHot)
            } else if (res in 25..30) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothSuperHot)
            } else if (res in 31..55) {
                mainViewModel.setMyModelList(arraysGarb.mListNameClothHardHot)
            } else {
                mainViewModel.setMyModelList(arraysGarb.mListNameCloth)
            }
        }
    }
}

