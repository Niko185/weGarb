package com.example.wegarb.presentation.view.fragments.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.databinding.FragmentDetailsHistoryBinding
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.presentation.view.fragments.history.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsHistoryFragment : Fragment() {
   private lateinit var binding: FragmentDetailsHistoryBinding
   private lateinit var detailsHistoryAdapter: DetailsHistoryAdapter
    private val detailsHistoryViewModel: DetailsHistoryViewModel by viewModels()
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        showDetailsHistoryDayInfo()
    }

    private fun getHistoryDayInfo(historyDay: HistoryDay) {
        binding.textCityName.text = historyDay.cityName
        binding.textDateAndTime.text = historyDay.date
        binding.textStatus.text = "Status day: ${historyDay.status}"
        binding.textTemperature.text = "${historyDay.temperature}°C"
        binding.textFeltTemperature.text = "Felt temperature: ${historyDay.feltTemperature}°C"
        binding.textWindSpeed.text = "Wind speed: ${historyDay.windSpeed} m/c"
        binding.textWindDirection.text = detailsHistoryViewModel.getWindDirectionName(historyDay.windDirection.toInt())
        binding.textDescription.text = "Description: ${historyDay.description}"
        detailsHistoryAdapter.submitList(historyDay.clothingList)
    }

    private fun showDetailsHistoryDayInfo(){
        arguments?.let {
            val historyDay = requireArguments().get("historyDay") as? HistoryDay
            historyDay?.let {
                getHistoryDayInfo(it)
            }
        }
    }

    private fun initRecyclerView() {
        detailsHistoryAdapter = DetailsHistoryAdapter()
        binding.rcViewDetails.adapter = detailsHistoryAdapter
    }


}
