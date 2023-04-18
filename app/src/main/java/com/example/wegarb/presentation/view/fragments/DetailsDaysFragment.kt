package com.example.wegarb.presentation.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.wegarb.R
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.databinding.FragmentDetailsDaysBinding
import com.example.wegarb.presentation.vm.MainViewModel

class DetailsDaysFragment : Fragment() {
   private lateinit var binding: FragmentDetailsDaysBinding
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
        getSavedDataAdditional()
    }

    private fun getSavedData() = with(binding){
        mainViewModel.mutableSavedModel.observe(viewLifecycleOwner){
            textCity.text = it.currentCity
            textDateAndTime.text = it.date
            textTemperature.text = it.currentTemp
            textCondition.text = it.currentCondition
            textWind.text = it.currentWind
            textStatus.text = it.status
        }
    }

    private fun getSavedDataAdditional() = with(binding){
        mainViewModel.mutableHeadModel.observe(viewLifecycleOwner){
            textFeelsLike.text = it.cFellsLike
            textWindDirection.text = it.windVariant
            textHumidity.text = it.humidity
        }
    }
    companion object {

        @JvmStatic
        fun newInstance() = DetailsDaysFragment()

    }
}