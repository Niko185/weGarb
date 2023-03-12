package com.example.wegarb.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.wegarb.databinding.FragmentAccountBinding
import com.example.wegarb.utils.isPermissionGranted
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wegarb.R
import com.example.wegarb.data.GarbModel
import com.example.wegarb.data.WeatherModel
import com.example.wegarb.data.WeatherModelCityName
import com.example.wegarb.utils.GpsDialog
import com.example.wegarb.view.adapters.GarbAdapter
import com.example.wegarb.vm.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var garbAdapter: GarbAdapter
    private val mainViewModel: MainViewModel by activityViewModels()

    private val mListNameCloth = mutableListOf(
        GarbModel("Beanie", R.drawable.garb_beanie),
        GarbModel("Cap", R.drawable.garb_cap),
        GarbModel("Gloves", R.drawable.garb_gloves),
        GarbModel("Hoodie", R.drawable.garb_hoodie),
        GarbModel("Jacket", R.drawable.garb_jacket),
        GarbModel("Jeans", R.drawable.garb_jeans),
        GarbModel("Mittens", R.drawable.garb_mittens),
        GarbModel("Raincoat", R.drawable.garb_raincoat),
        GarbModel("Shorts", R.drawable.garb_shorts),
        GarbModel("Sunglasses", R.drawable.garb_sunglasses),
        GarbModel("Thermal kit", R.drawable.garb_thermal_kit),
        GarbModel("Tight sweater", R.drawable.garb_tight_sweater),
        GarbModel("Tight windbreaker", R.drawable.garb_tight_windbreaker),
        GarbModel("T-shirt", R.drawable.garb_tshirt),
        GarbModel("Turtleneck", R.drawable.garb_turtleneck),
        GarbModel("Umbrella", R.drawable.garb_umbrella),
        GarbModel("Windbreaker", R.drawable.garb_windbreaker),
        GarbModel("Winter scarf", R.drawable.garb_winter_scarf),
        GarbModel("Balaclava", R.drawable.garb_balaclava),
        GarbModel("Bomber", R.drawable.garb_bomber),
        GarbModel("Denim jacket", R.drawable.garb_denim_jacket),
        GarbModel("Fleece jacket", R.drawable.garb_fleece),
        GarbModel("Light beanie", R.drawable.garb_light_beanie),
        GarbModel("Long winter jacket", R.drawable.garb_long_winter_jacket),
        GarbModel("Neck gaiter", R.drawable.garb_neck_gaiter),
        GarbModel("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
        GarbModel("Winter jacket", R.drawable.garb_puffer_coat),
        GarbModel("Rain boots", R.drawable.garb_rain_boots),
        GarbModel("Sandals", R.drawable.garb_sandals),
        GarbModel("Sneakers", R.drawable.garb_sneakers),
        GarbModel("Show boots", R.drawable.garb_snow_boot),
        GarbModel("Snow pants", R.drawable.garb_snow_pants),
        GarbModel("Light pants", R.drawable.garb_summer_pants),
        GarbModel("Sunscreen", R.drawable.garb_sunscreen),
        GarbModel("Long snow boots", R.drawable.garb_super_show_boots),
        GarbModel("Super winter coat", R.drawable.garb_super_winter_coat),
        GarbModel("Thermal socks", R.drawable.garb_thermo_socks),
        GarbModel("Thermos", R.drawable.garb_thermos),
        GarbModel("Water bottle", R.drawable.garb_water_bottle),
        GarbModel("White summer hat", R.drawable.garb_white_summer_hat),
        GarbModel("Light windbreaker", R.drawable.garb_light_windbreaker),
        GarbModel("Winter ointment", R.drawable.garb_winter_ointment)
    )

    private val mListNameGarbHardCold = mutableListOf(
        GarbModel("Thermal kit", R.drawable.garb_thermal_kit),
        GarbModel("Thermal socks", R.drawable.garb_thermo_socks),
        GarbModel("Long snow boots", R.drawable.garb_super_show_boots),
        GarbModel("Super winter coat", R.drawable.garb_super_winter_coat),
        GarbModel("Beanie", R.drawable.garb_beanie),
        GarbModel("Fleece jacket", R.drawable.garb_fleece),
        GarbModel("Tight sweater", R.drawable.garb_tight_sweater),
        GarbModel("Balaclava", R.drawable.garb_balaclava),
        GarbModel("Winter scarf", R.drawable.garb_winter_scarf),
        GarbModel("Neck gaiter", R.drawable.garb_neck_gaiter),
        GarbModel("Snow pants", R.drawable.garb_snow_pants),
        GarbModel("Mittens", R.drawable.garb_mittens),
        GarbModel("Gloves", R.drawable.garb_gloves),
        GarbModel("Thermos", R.drawable.garb_thermos),
        GarbModel("Winter ointment", R.drawable.garb_winter_ointment)
    )

    private val mListNameGarbSuperCold = mutableListOf(
        GarbModel("Thermal kit", R.drawable.garb_thermal_kit),
        GarbModel("Snow pants", R.drawable.garb_snow_pants),
        GarbModel("Long snow boots", R.drawable.garb_super_show_boots),
        GarbModel("Turtleneck", R.drawable.garb_turtleneck),
        GarbModel("Hoodie", R.drawable.garb_hoodie),
        GarbModel("Long winter jacket", R.drawable.garb_long_winter_jacket),
        GarbModel("Beanie", R.drawable.garb_beanie),
        GarbModel("Balaclava", R.drawable.garb_balaclava),
        GarbModel("Mittens", R.drawable.garb_mittens),
        GarbModel("Thermos", R.drawable.garb_thermos)
    )
    private val mListNameGarbCold = mutableListOf(
        GarbModel("Thermal kit", R.drawable.garb_thermal_kit),
        GarbModel("Snow pants", R.drawable.garb_snow_pants),
        GarbModel("Show boots", R.drawable.garb_snow_boot),
        GarbModel("Hoodie", R.drawable.garb_hoodie),
        GarbModel("Winter scarf", R.drawable.garb_winter_scarf),
        GarbModel("Mittens", R.drawable.garb_mittens),
        GarbModel("Beanie", R.drawable.garb_beanie),
        GarbModel("Long winter jacket", R.drawable.garb_long_winter_jacket),
        GarbModel("Thermos", R.drawable.garb_thermos)
    )

    private val mListNameGarbNormalCold = mutableListOf(
        GarbModel("Thermal kit", R.drawable.garb_thermal_kit),
        GarbModel("Beanie", R.drawable.garb_beanie),
        GarbModel("Show boots", R.drawable.garb_snow_boot),
        GarbModel("Tight sweater", R.drawable.garb_tight_sweater),
        GarbModel("Winter scarf", R.drawable.garb_winter_scarf),
        GarbModel("Jeans", R.drawable.garb_jeans),
        GarbModel("Winter jacket", R.drawable.garb_puffer_coat),
        GarbModel("Gloves", R.drawable.garb_gloves)
    )

    private val mListNameGarbTransitionCold = mutableListOf(
        GarbModel("Jeans", R.drawable.garb_jeans),
        GarbModel("Light beanie", R.drawable.garb_light_beanie),
        GarbModel("Rain boots", R.drawable.garb_rain_boots),
        GarbModel("T-shirt", R.drawable.garb_tshirt),
        GarbModel("Turtleneck", R.drawable.garb_turtleneck),
        GarbModel("Winter jacket", R.drawable.garb_puffer_coat),
        GarbModel("Gloves", R.drawable.garb_gloves)
    )

    private val mListNameGarbTransitionHot = mutableListOf(
        GarbModel("Jeans", R.drawable.garb_jeans),
        GarbModel("Sneakers", R.drawable.garb_sneakers),
        GarbModel("T-shirt", R.drawable.garb_tshirt),
        GarbModel("Tight sweater", R.drawable.garb_tight_sweater),
        GarbModel("Bomber", R.drawable.garb_bomber),
        GarbModel("Cap", R.drawable.garb_cap)
    )

    private val mListNameGarbNormalHot = mutableListOf(
        GarbModel("Light pants", R.drawable.garb_summer_pants),
        GarbModel("Sneakers", R.drawable.garb_sneakers),
        GarbModel("T-shirt", R.drawable.garb_tshirt),
        GarbModel("Denim jacket", R.drawable.garb_denim_jacket),
        GarbModel("Cap", R.drawable.garb_cap)
    )

    private val mListNameClothHot = mutableListOf(
        GarbModel("Sneakers", R.drawable.garb_sneakers),
        GarbModel("Shorts", R.drawable.garb_shorts),
        GarbModel("T-shirt", R.drawable.garb_tshirt),
        GarbModel("Cap", R.drawable.garb_cap),
        GarbModel("Sunglasses", R.drawable.garb_sunglasses)
    )
    private val mListNameClothSuperHot = mutableListOf(
        GarbModel("Sandals", R.drawable.garb_sandals),
        GarbModel("Shorts", R.drawable.garb_shorts),
        GarbModel("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
        GarbModel("White summer hat", R.drawable.garb_white_summer_hat),
        GarbModel("Water bottle", R.drawable.garb_water_bottle),
        GarbModel("Sunglasses", R.drawable.garb_sunglasses)
    )
    private val mListNameClothHardHot = mutableListOf(
        GarbModel("Sandals", R.drawable.garb_sandals),
        GarbModel("Shorts", R.drawable.garb_shorts),
        GarbModel("Oversize t-shirt", R.drawable.garb_oversize_tie_dye),
        GarbModel("White summer hat", R.drawable.garb_white_summer_hat),
        GarbModel("Water bottle", R.drawable.garb_water_bottle),
        GarbModel("Sunglasses", R.drawable.garb_sunglasses),
        GarbModel("Sunscreen", R.drawable.garb_sunscreen),
    )


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

    private fun formatterUnix(unixTime: String): String {
        val unixSeconds = unixTime.toLong()
        val date = Date(unixSeconds * 1000)
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = sdf.format(date)
        return formattedDate.toString()
    }

    private fun requestApiCityName(latitude: String, longitude: String) {
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
            Log.d("Mylog", "thhis $res")
            if (res in -60..-35) {
                mainViewModel.setMyModelList(mListNameGarbHardCold)
            } else if (res in -34..-27) {
                mainViewModel.setMyModelList(mListNameGarbSuperCold)
            } else if (res in -26..-15) {
                mainViewModel.setMyModelList(mListNameGarbCold)
            } else if (res in -14..-5) {
                mainViewModel.setMyModelList(mListNameGarbNormalCold)
            } else if (res in -4..8) {
                mainViewModel.setMyModelList(mListNameGarbTransitionCold)
            } else if (res in 9..14) {
                mainViewModel.setMyModelList(mListNameGarbTransitionHot)
            } else if (res in 15..18) {
                mainViewModel.setMyModelList(mListNameGarbNormalHot)
            } else if (res in 19..24) {
                mainViewModel.setMyModelList(mListNameClothHot)
            } else if (res in 25..30) {
                mainViewModel.setMyModelList(mListNameClothSuperHot)
            } else if (res in 31..55) {
                mainViewModel.setMyModelList(mListNameClothHardHot)
            } else {
                mainViewModel.setMyModelList(mListNameCloth)
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

    private fun getMyLocationNow() {
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
}

