package com.example.wegarb.presentation.view.fragments.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.databinding.FragmentDetailsDaysBinding
import com.example.wegarb.presentation.utils.DialogManager.getWindDirection
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel

class DetailsHistoryFragment : Fragment() {
   private lateinit var binding: FragmentDetailsDaysBinding
   private lateinit var detailsHistoryAdapter: DetailsHistoryAdapter
    private val weatherViewModel: WeatherViewModel by activityViewModels{
        WeatherViewModel.WeatherViewModelFactory((requireContext().applicationContext as AppDatabaseInstance).database)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSavedData()
        initRecyclerView()
        showDetailsHistory()
    }

    private fun getSavedData() = with(binding){
        weatherViewModel.savedFullDaysInformation.observe(viewLifecycleOwner) {

            val cCity = it.cityName
            val cDateAndTime = it.date
            val cTemperature = "${it.temperature}°C"
            val cCondition = "Direction: ${it.description}"
            val cSearchWindDto = "SearchWindDto speed: ${it.windSpeed}"
            val cStatus = it.status
            val cFeelsLike = "Felt temperature: ${it.feltTemperature}°C"
            val cWindDirection = getWindDirection(it.windDirection.toInt())
            val cHumidity = "Humidity: ${it.humidity}%"

            textCity.text = it.cityName
            textDateAndTime.text = cDateAndTime
            textTemperature.text = cTemperature
            textCondition.text = cCondition
            textWind.text = cSearchWindDto
            textStatus.text = cStatus
            textFeelsLike.text = cFeelsLike
            textWindDirection.text = cWindDirection
            textHumidity.text = "Humidity: ${it.humidity}%"
        }
    }


    private fun initRecyclerView() = with(binding) {
        rcViewDetails.layoutManager = GridLayoutManager(activity, 2)
        detailsHistoryAdapter = DetailsHistoryAdapter()
        rcViewDetails.adapter = detailsHistoryAdapter
    }

    private fun showDetailsHistory(){
        weatherViewModel.savedFullDaysInformation.observe(viewLifecycleOwner) {
            detailsHistoryAdapter.submitList(it.wardrobeElementList)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailsHistoryFragment()
    }
}
