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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.databinding.FragmentWeatherBinding
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth.element_kit.WardrobeElement
import com.example.wegarb.domain.models.weather.Weather
import com.example.wegarb.presentation.utils.AdditionalWeatherDialog
import com.example.wegarb.presentation.utils.GpsDialog
import com.example.wegarb.presentation.utils.SaveHistoryDayDialog
import com.example.wegarb.presentation.utils.SearchCityDialog
import com.example.wegarb.utils.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.util.*

class WeatherFragment : Fragment(), WeatherAdapter.Listener {
    private lateinit var binding: FragmentWeatherBinding
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter
    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.WeatherViewModelFactory((requireContext().applicationContext as AppDatabaseInstance).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getMyLocationCoordinate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initLocationClient()
        getMyLocationNow()
        showLocationWeather()
        initRecyclerView()
        showDataInRecyclerView()
        showSearchWeather()
        onClickSearch()
        onClickMyLocation()
        getStatusForSaveHistoryDay()
        showAdditionalWeather()
    }

    @SuppressLint("SetTextI18n")
    private fun showLocationWeather() {
        weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text = weatherViewModel.formatterUnix(it.date)
            binding.tvCurrentTemperature.text = "${it.temperature}°C"
            binding.tvCurrentCondition.text = "Description:  ${it.description}"
            binding.tvCurrentWind.text = "Wind speed:  ${it.windSpeed} m/c"
            binding.tvCurrentCoordinate.text = "(lat:${it.latitude} / lon:${it.longitude})"
            binding.tvCityName.text = "City: ${it.city}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSearchWeather() {
        weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text = weatherViewModel.formatterUnix(it.date)
            binding.tvCurrentTemperature.text = "${it.temperature}°C"
            binding.tvCurrentCondition.text = "Description:  ${it.description}"
            binding.tvCurrentWind.text = "Wind speed:  ${it.windSpeed} m/c"
            binding.tvCurrentCoordinate.text = "(lat:${it.latitude} / lon:${it.longitude})"
            binding.tvCityName.text = "City: ${it.city}"
        }
    }

    private fun showAdditionalWeather() {
        binding.headCard.setOnClickListener {
            AdditionalWeatherDialog.start(requireContext(), weatherViewModel.getAdditionalWeather())
        }
    }

    private fun getStatusForSaveHistoryDay() {
        binding.buttonSaveHistoryDay.setOnClickListener {
            SaveHistoryDayDialog.start(requireContext(), object : SaveHistoryDayDialog.Listener {
                override fun onClickComfort() {
                    weatherViewModel.onClickSaveHistoryDay("Comfort")
                }

                override fun onClickCold() {
                    weatherViewModel.onClickSaveHistoryDay("Cold")
                }

                override fun onClickHot() {
                    weatherViewModel.onClickSaveHistoryDay("Hot")
                }
            })
        }
    }

    private fun onClickMyLocation() {
        binding.buttonMyLocation.setOnClickListener {
            getMyLocationCoordinate()
            Toast.makeText(activity, "Weather forecast update for your location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClickSearch() {
        binding.buttonSearchCity.setOnClickListener {
            weatherViewModel.type = "search"
            SearchCityDialog.start(requireContext(), object : SearchCityDialog.Listener {
                override fun searchCity(cityName: String?) {
                    cityName.let { weatherViewModel.getSearchWeather(cityName.toString())
                                            weatherViewModel.type = "search"
                    }
                }
            })
        }
    }

    override fun onClickItemInRecyclerView(wardrobeElement: WardrobeElement) {
        weatherViewModel.openDialog(requireContext(), wardrobeElement)
    }

    private fun initRecyclerView() = with(binding) {
        weatherAdapter = WeatherAdapter(this@WeatherFragment)
        rcViewGarb.adapter = weatherAdapter
    }

    private fun showDataInRecyclerView() {
        weatherViewModel.clothingList.observe(viewLifecycleOwner) {
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
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        }
    }

    private fun initLocationClient() {
        locationClientLauncher = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun getMyLocationNow() {
        if (isGpsEnable()) {
            getMyLocationCoordinate()
        } else {
            GpsDialog.start(requireContext(), object : GpsDialog.ActionWithUser {
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
        .addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                weatherViewModel.initRetrofit()
                val location = task.result
                weatherViewModel.getLocationWeather(location.latitude, location.longitude)
                weatherViewModel.type = "location"
            } else {
                val latitude = 00.5454
                val longitude = 00.3232
                weatherViewModel.getLocationWeather(latitude, longitude)
                weatherViewModel.type = "location"
            }
        }

    }



















}




