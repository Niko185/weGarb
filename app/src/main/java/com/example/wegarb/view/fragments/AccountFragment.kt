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
import android.app.LocaleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.wegarb.data.WeatherModel
import com.example.wegarb.utils.GpsDialog
import com.example.wegarb.vm.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

const val API_KEY = "f054c52de0a9f5d1e50b480bdd0aee4f"

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
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
        showDataOnScreen()
        requestApiNameCity()
    }

    override fun onResume() {
        super.onResume()
        setMyLocationNow()
    }

    // showData Functions
    private fun showDataOnScreen() = with(binding) {
        mainViewModel.currentLiveDataHeadModel.observe(viewLifecycleOwner) {
            tvCurrentData.text = mainViewModel.currentLiveDataHeadModel.value?.currentData.toString()
            tvCurrentTemperature.text = mainViewModel.currentLiveDataHeadModel.value?.currentTemperature.toString()
            tvCurrentWind.text = mainViewModel.currentLiveDataHeadModel.value?.currentWind.toString()
            tvCurrentCity.text = mainViewModel.currentLiveDataHeadModel.value?.currentCity.toString()
            tvCurrentCondition.text = mainViewModel.currentLiveDataHeadModel.value?.currentCondition.toString()
        }
    }

    private fun requestApiNameCity(city: String) {
        val urlCity = "https://api.openweathermap.org/geo/1.0/reverse?lat=58.0104600&lon=56.2501700&limit=1&appid=$API_KEY"
        val queueCity = Volley.newRequestQueue(context)

        val mainRequestCity = StringRequest(
            Request.Method.GET,
            urlCity,
            { responseCity -> Log.d("Mylog", "resp: $responseCity" ) },
            { errorCity -> Log.d("Mylog", "error: $errorCity") }
        )
        queueCity.add(mainRequestCity)

    }

    // API Functions
    private fun requestApi(latitude: String, longitude: String) {
        val url = "https://api.openweathermap.org/data/3.0/onecall?lat=$latitude&lon=$longitude&units=metric&exclude=&appid=$API_KEY"
       // val url = "https://api.openweathermap.org/geo/1.0/reverse?lat=$latitude&lon=$longitude&limit=1&appid=$API_KEY"
        val queue = Volley.newRequestQueue(context)


        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response -> getMainResponse(response) },
            { error -> Log.d("Mylog", "error: $error") },

        )
        queue.add(mainRequest)
    }

    private fun getMainResponse(response: String) {
        val responseJson = JSONObject(response)
        parsingApi(responseJson)
    }

    private fun parsingApi(responseJson: JSONObject) {

        val currentDataHeadRequest = responseJson.getJSONObject("current").getString("dt")
        val currentDataHead = formatterUnix(currentDataHeadRequest)

        val currentTemperatureHead = responseJson.getJSONObject("current").getString("temp")

        val currentWindHead = responseJson.getJSONObject("current").getString("wind_speed")

        val currentCityHeadRequestLatitude = responseJson.getString("lat")
        val currentCityHeadRequestLongitude = responseJson.getString("lon")
        val currentCityHead = "$currentCityHeadRequestLatitude / $currentCityHeadRequestLongitude"

        val currentConditionHeadRequest = responseJson.getJSONObject("current").getJSONArray("weather")
        val currentConditionHeadRequestObject = currentConditionHeadRequest[0] as JSONObject
        val currentConditionHead = currentConditionHeadRequestObject.getString("main")


        val headCardModel = WeatherModel(
             currentDataHead,
             currentTemperatureHead,
             currentWindHead,
             currentCityHead,
             currentConditionHead
        )
        mainViewModel.currentLiveDataHeadModel.value = headCardModel
    }

    private fun formatterUnix(unixTime: String): String {
           val unixSeconds = unixTime.toLong()
           val date = Date(unixSeconds * 1000)
           val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
          sdf.timeZone = TimeZone.getTimeZone("GMT-0")
           val formattedDate = sdf.format(date)
           return formattedDate.toString()
    }


    // // Permissions, Gps & Location - Functions
    private fun checkPermission() {
        if(!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            responsePermissionDialog()
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun responsePermissionDialog() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }
    }

  private  fun setMyLocationNow(){
    if(isGpsEnable()) {
            getMyLocationCoordinate()
         } else {
             GpsDialog.startDialog(requireContext(), object : GpsDialog.ActionWithUser{
                 override fun transferUserGpsSettings() {
                     startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                 }
             })
         }
    }

    private fun isGpsEnable(): Boolean {
        val gpsCheck = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return gpsCheck.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun initLocationClient() {
        locationClientLauncher = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun getMyLocationCoordinate(){
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
        locationClientLauncher.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnCompleteListener {
                requestApi("${it.result.latitude}", "${it.result.longitude}")
            }
    }

    // Instance Fragment
    companion object {
        @JvmStatic
        fun newInstance() = AccountFragment()
    }
}