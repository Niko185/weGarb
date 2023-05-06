package com.example.wegarb.presentation.view.fragments.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.databinding.FragmentDetailsDaysBinding
import com.example.wegarb.presentation.utils.DialogManager.getWindDirection
import com.example.wegarb.presentation.vm.MainViewModel

class DetailsHistoryFragment : Fragment() {
   private lateinit var binding: FragmentDetailsDaysBinding
   private lateinit var detailsHistoryAdapter: DetailsHistoryAdapter
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as MainDataBaseInitialization).mainDataBaseInitialization)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        showInfoObserve()
    }

    private fun getSavedData() = with(binding){
        mainViewModel.savedFullDaysInformation.observe(viewLifecycleOwner) {
            val cCity = it.currentCity
            val cDateAndTime = it.date
            val cTemperature = "${it.currentTemp}°C"
            val cCondition = "Direction: ${it.currentCondition}"
            val cWind = "Wind speed: ${it.currentWind}"
            val cStatus = it.status
            val cFeelsLike = "Felt temperature: ${it.currentFeelsLike}°C"
            val cWindDirection = getWindDirection(it.windDirection.toInt())
            val cHumidity = "Humidity: ${it.humidity}%"

            textCity.text = cCity
            textDateAndTime.text = cDateAndTime
            textTemperature.text = cTemperature
            textCondition.text = cCondition
            textWind.text = cWind
            textStatus.text = cStatus
            textFeelsLike.text = cFeelsLike
            textWindDirection.text = cWindDirection
            textHumidity.text = cHumidity
        }
    }


    private fun initRecyclerView() = with(binding) {
        rcViewDetails.layoutManager = GridLayoutManager(activity, 2)
        detailsHistoryAdapter = DetailsHistoryAdapter()
        rcViewDetails.adapter = detailsHistoryAdapter
    }

    private fun showInfoObserve(){
        mainViewModel.savedFullDaysInformation.observe(viewLifecycleOwner) {
            detailsHistoryAdapter.submitList(it.garb)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailsHistoryFragment()
    }
}
