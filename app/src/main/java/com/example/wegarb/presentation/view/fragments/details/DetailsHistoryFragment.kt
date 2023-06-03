package com.example.wegarb.presentation.view.fragments.details

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wegarb.databinding.FragmentDetailsHistoryBinding
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsHistoryFragment : Fragment() {
   private lateinit var binding: FragmentDetailsHistoryBinding
   private lateinit var detailsHistoryAdapter: DetailsHistoryAdapter
   private val viewModel: WeatherViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("Teg", "${viewModel.hashCode()}")
        getSavedData()
        initRecyclerView()
        showDetailsHistory()
    }

    @SuppressLint("SetTextI18n")
    private fun getSavedData() = with(binding){
        viewModel.fullDayInformation.observe(viewLifecycleOwner) {
            textCityName.text = it.cityName
            textDateAndTime.text = it.date
            textTemperature.text = "${it.temperature}°C"
            textDescription.text = "Description: ${it.description}"
            textWindSpeed.text = "Wind speed: ${it.windSpeed} m/c"
            textStatus.text = "Status day: ${it.status}"
            textFeltTemperature.text = "Felt temperature: ${it.feltTemperature}°C"
            textWindDirection.text = viewModel.getWindDirection(it.windDirection.toInt())
        }
    }


    private fun initRecyclerView() = with(binding) {
        rcViewDetails.layoutManager = GridLayoutManager(activity, 2)
        detailsHistoryAdapter = DetailsHistoryAdapter()
        rcViewDetails.adapter = detailsHistoryAdapter
    }

    private fun showDetailsHistory(){
        viewModel.fullDayInformation.observe(viewLifecycleOwner) {
            detailsHistoryAdapter.submitList(it.clothingList)
        }
    }


}
