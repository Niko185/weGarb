package com.example.wegarb.presentation.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wegarb.data.arrays.ArraysGarb
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.data.models.GarbModel
import com.example.wegarb.databinding.FragmentDetailsDaysBinding
import com.example.wegarb.presentation.view.adapters.DetailsAdapter
import com.example.wegarb.presentation.vm.MainViewModel

class DetailsDaysFragment : Fragment() {
   private lateinit var binding: FragmentDetailsDaysBinding
   private lateinit var detailsAdapter: DetailsAdapter
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
        mainViewModel.mutableSavedModel.observe(viewLifecycleOwner) {
            val cCity = it.currentCity
            val cDateAndTime = it.date
            val cTemperature = "${it.currentTemp}°C"
            val cCondition = "Condition: ${it.currentCondition}"
            val cWind = "Wind: ${it.currentWind} m/c"
            val cStatus = "Status: ${it.status}"
            val cFeelsLike = "Feels like: ${it.currentFeelsLike}°C"
            val cWindDirection = "(direction: ${it.windDirection})"
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
        detailsAdapter = DetailsAdapter()
        rcViewDetails.adapter = detailsAdapter
    }

    private fun showInfoObserve(){
        mainViewModel.mutableSavedModel.observe(viewLifecycleOwner) {
            detailsAdapter.submitList(it.garb)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailsDaysFragment()
    }
}
