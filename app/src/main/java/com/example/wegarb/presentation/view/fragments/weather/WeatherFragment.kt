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
import com.example.wegarb.databinding.FragmentWeatherBinding
import com.example.wegarb.domain.models.*
import com.example.wegarb.domain.models.cloth_kits.element_kit.WardrobeElement
import com.example.wegarb.domain.models.name_direction.WindDirection
import com.example.wegarb.presentation.utils.DialogManager
import com.example.wegarb.presentation.utils.GpsDialog
import com.example.wegarb.presentation.utils.SearchDialog
import com.example.wegarb.utils.isPermissionGranted
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import java.text.SimpleDateFormat
import java.util.*



class WeatherFragment : Fragment(), WeatherAdapter.Listener {
    private lateinit var binding: FragmentWeatherBinding
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy - HH:mm")
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationClientLauncher: FusedLocationProviderClient
    private lateinit var weatherAdapter: WeatherAdapter
    private val baseClothesKit: BaseClothesKit = BaseClothesKit()
    private val rainClothesKit: RainClothesKit = RainClothesKit()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initLocationClient()
        initRcViewGarb()
        showDataInRcViewOnScreenObserver()

        showLocationWeather()
        getLocationClothKit()
        clickSaveLocationDay()

        showSearchWeather()
        getSearchClothKit()
        clickSaveSearchDay()

        clickSearch()
        clickMyLocation()
    }

    override fun onResume() {
        super.onResume()
        getMyLocationNow()
    }

    private fun clickMyLocation() {
        binding.buttonMyLocation.setOnClickListener {
            getMyLocationNow()
        }
    }

    private fun clickSearch() {
        binding.buttonSearchCity.setOnClickListener {
            SearchDialog.searchCityDialog(requireContext(), object : SearchDialog.Listener {
                override fun searchCity(cityName: String?) {
                    cityName.let { weatherViewModel.getSearchWeather(cityName.toString()) }
                }
            })
        }
    }

    // Отображаем прогноз погоды в HeadCardView на первом фрагменте
    @SuppressLint("SetTextI18n")
    private fun showLocationWeather() {
        weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text = formatterUnix(it.date)
            binding.tvCurrentTemperature.text = "${it.temperature}°C"
            binding.tvCurrentCondition.text = "Description:  ${it.description}"
            binding.tvCurrentWind.text = "Wind speed:  ${it.windSpeed} m/c"
            binding.tvCurrentCoordinate.text = "(lat:${it.latitude} / lon:${it.longitude})"
            binding.tvCityName.text = "City: ${it.ctiy?.name}"
        }
    }

    // Получаем список одежды исходя из данных которые пришли в HeadCardView.
    // Отображаем список в recyclerView на первом фрагменте
    private fun getLocationClothKit(): List<WardrobeElement> {
        val clothesList = mutableListOf<WardrobeElement>()
        weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
            val res = weatherViewModel.locationWeather.value?.temperature
            val conditionRainResponse = weatherViewModel.locationWeather.value?.description
            val conditionRainList = mutableListOf("Rain")
            val selectedClothesKit = when {
                res in -60..-35 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
                res in -60..-35 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
                res in -34..-27 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
                res in -34..-27 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
                res in -26..-15 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
                res in -26..-15 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
                res in -14..-5 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
                res in -14..-5 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
                res in -4..8 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
                res in -4..8 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
                res in 9..14 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
                res in 9..14 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
                res in 15..18 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
                res in 15..18 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
                res in 19..24 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
                res in 19..24 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
                res in 25..30 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
                res in 25..30 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
                res in 31..55 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot
                res in 31..55 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot

                else -> null
            }
            if (selectedClothesKit != null) {
                clothesList.addAll(
                    if (conditionRainResponse !in conditionRainList) {
                        weatherViewModel.getListWardrobeElements(selectedClothesKit.first)
                    } else {
                        weatherViewModel.getListWardrobeElements(selectedClothesKit.second)
                    }
                )
            }
        }
        return clothesList
    }


    // Формируем сохранение данных(Весь прогноз погоды и Набор Одежды)
    private fun clickSaveLocationDay() {
        weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
            binding.buttonSaveHistoryDay.setOnClickListener {
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
                                cityName = it.ctiy?.name ?: "not found city name",
                                status = getStatusComfort(),
                                humidity = it.humidity,
                                wardrobeElementList = getLocationClothKit()
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
                                cityName = it.ctiy?.name ?: "not found city name",
                                status = getStatusCold(),
                                humidity = it.humidity,
                                wardrobeElementList = getLocationClothKit()
                            )
                            weatherViewModel.insertFullDayInformation(historyDayEntity)
                        }
                    }

                    override fun onClickHot() {
                        weatherViewModel.locationWeather.observe(viewLifecycleOwner) {
                            val historyDayEntity = HistoryDayEntity(
                                id = null,
                                date = getDate(),
                                temperature = it.temperature.toString(),
                                feltTemperature = it.feltTemperature.toString(),
                                description = it.description,
                                windSpeed = it.windSpeed,
                                windDirection = it.windDirection,
                                cityName = it.ctiy?.name ?: "not found city name",
                                status = getStatusHot(),
                                humidity = it.humidity,
                                wardrobeElementList = getLocationClothKit()
                            )
                            weatherViewModel.insertFullDayInformation(historyDayEntity)
                        }
                    }
                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showSearchWeather() {
        weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
            binding.tvCurrentData.text = formatterUnix(it.date)
            binding.tvCurrentTemperature.text = "${it.temperature}°C"
            binding.tvCurrentCondition.text = "Direction:  ${it.description}"
            binding.tvCurrentWind.text = "Wind speed:  ${it.windSpeed} m/c"
            binding.tvCurrentCoordinate.text =
                "(lat:${it.currentLatitude} / lon:${it.currentLongitude})"
            binding.tvCityName.text = "City:  ${it.cityName}"
        }
    }


    private fun getSearchClothKit(): List<WardrobeElement> {
        val clothesList = mutableListOf<WardrobeElement>()
        weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
            val res = weatherViewModel.searchWeather.value?.temperature
            val conditionRainResponse = weatherViewModel.searchWeather.value?.description
            val conditionRainList = mutableListOf("Rain")
            val selectedClothesKit = when {
                res in -60..-35 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
                res in -60..-35 && conditionRainResponse in conditionRainList -> baseClothesKit.kitHardCold to rainClothesKit.kitRainHardCold
                res in -34..-27 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
                res in -34..-27 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperCold to rainClothesKit.kitRainSuperCold
                res in -26..-15 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
                res in -26..-15 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryCold to rainClothesKit.kitRainVeryCold
                res in -14..-5 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
                res in -14..-5 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalCold to rainClothesKit.kitRainNormalCold
                res in -4..8 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
                res in -4..8 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitCold to rainClothesKit.kitRainTransitCold
                res in 9..14 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
                res in 9..14 && conditionRainResponse in conditionRainList -> baseClothesKit.kitTransitHot to rainClothesKit.kitRainTransitHot
                res in 15..18 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
                res in 15..18 && conditionRainResponse in conditionRainList -> baseClothesKit.kitNormalHot to rainClothesKit.kitRainNormalHot
                res in 19..24 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
                res in 19..24 && conditionRainResponse in conditionRainList -> baseClothesKit.kitVeryHot to rainClothesKit.kitRainVeryHot
                res in 25..30 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
                res in 25..30 && conditionRainResponse in conditionRainList -> baseClothesKit.kitSuperHot to rainClothesKit.kitRainSuperHot
                res in 31..55 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot
                res in 31..55 && conditionRainResponse !in conditionRainList -> baseClothesKit.kitHardHot to rainClothesKit.kitRainHardHot

                else -> null
            }
            if (selectedClothesKit != null) {
                clothesList.addAll(
                    if (conditionRainResponse !in conditionRainList) {
                        weatherViewModel.getListWardrobeElements(selectedClothesKit.first)

                    } else {
                        weatherViewModel.getListWardrobeElements(selectedClothesKit.second)
                    }
                )
            }
        }
        return clothesList
    }

    private fun clickSaveSearchDay() {
        weatherViewModel.searchWeather.observe(viewLifecycleOwner) {
            binding.buttonSaveHistoryDay.setOnClickListener {
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
                                wardrobeElementList = getSearchClothKit()
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
                                wardrobeElementList = getSearchClothKit()
                            )
                            weatherViewModel.insertFullDayInformation(historyDayEntity)
                        }
                    }

                    override fun onClickHot() {
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
                                status = getStatusHot(),
                                humidity = it.humidity,
                                wardrobeElementList = getSearchClothKit()
                            )
                            weatherViewModel.insertFullDayInformation(historyDayEntity)
                        }
                    }
                })
            }
        }
    }

    fun getStatusComfort(): String {
        return "Status: OKEY"
    }

    fun getStatusCold(): String {
        return "Status: COLD"
    }

    fun getStatusHot(): String {
        return "Status: HOT"
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
            .addOnCompleteListener { task ->

                weatherViewModel.initRetrofit()
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    weatherViewModel.getLocationWeather(location.latitude, location.longitude)
                } else {
                    val latitude = 00.5454
                    val longitude = 00.3232
                    weatherViewModel.getLocationWeather(latitude, longitude)
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
}
/*private fun showAdditionalLocationWeather(locationWeather: LocationWeather) {

            binding.headCard.setOnClickListener {
                DialogManager.showHeadDialogLocation(requireContext(), locationWeather)
            }
    }*/

/*
private fun showAdditionalSearchWeather(searchWeather: SearchWeather) {
    binding.headCard.setOnClickListener {
        DialogManager.showHeadDialogSearch(requireContext(), searchWeather)
    }
}
*/




