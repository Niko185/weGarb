package com.example.wegarb.presentation.view.fragments.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import com.example.wegarb.AppDatabaseInstance
import com.example.wegarb.data.AppDatabase
import com.example.wegarb.databinding.FragmentDaysBinding
import com.example.wegarb.presentation.view.fragments.details.DetailsHistoryFragment
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel
import com.example.wegarb.project_utils.FragmentManager


class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var myAdapter: HistoryAdapter
    private val weatherViewModel: WeatherViewModel by activityViewModels {
        WeatherViewModel.WeatherViewModelFactory((requireContext().applicationContext as AppDatabaseInstance).database)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcViewDays()
        observerForRcViewAndDataRcView()
    }


    private fun initRcViewDays() = with(binding) {
        rcViewDays.layoutManager = LinearLayoutManager(requireContext())
        myAdapter = HistoryAdapter(this@HistoryFragment)
        rcViewDays.adapter = myAdapter
    }

    private fun observerForRcViewAndDataRcView() {
       weatherViewModel.getAllDaysHistory.observe(viewLifecycleOwner) {
           myAdapter.submitList(it)
       }
    }


    override fun onClickViewOnItem(historyDayEntity: HistoryDayEntity) {
        weatherViewModel.deleteFullDayInformation(historyDayEntity)
    }

    override fun onClickViewOnItemAll(historyDayEntity: HistoryDayEntity) {
        weatherViewModel.savedFullDaysInformation.value = historyDayEntity
        FragmentManager.setFragment(DetailsHistoryFragment.newInstance(), activity as AppCompatActivity)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }



}