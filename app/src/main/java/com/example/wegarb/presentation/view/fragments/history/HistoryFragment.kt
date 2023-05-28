package com.example.wegarb.presentation.view.fragments.history

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.R

import com.example.wegarb.databinding.FragmentHistoryBinding
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel



class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var myAdapter: HistoryAdapter
    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.WeatherViewModelFactory((requireContext().applicationContext as AppDatabaseInstance).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcViewDays()
        observerForRcViewAndDataRcView()

    }


    private fun initRcViewDays() = with(binding) {
        rcViewHistoryDays.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = HistoryAdapter(this@HistoryFragment)
        rcViewHistoryDays.adapter = myAdapter
    }

    private fun observerForRcViewAndDataRcView() {
       weatherViewModel.historyDays.observe(viewLifecycleOwner)  {
           myAdapter.submitList(it)
       }
    }


    override fun onClickViewOnItem(historyDay: HistoryDay) {
        weatherViewModel.deleteHistoryDay(historyDay)
    }

    override fun onClickViewOnItemAll(historyDay: HistoryDay) {
        weatherViewModel.fullDayInformation.value = historyDay

        val navController = findNavController()
        navController.navigate(R.id.detailsHistoryFragment)

    }
}