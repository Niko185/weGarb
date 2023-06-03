package com.example.wegarb.presentation.view.fragments.history

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.R

import com.example.wegarb.databinding.FragmentHistoryBinding
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var myAdapter: HistoryAdapter
    private val viewModel: WeatherViewModel by viewModels()


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
       viewModel.historyDays.observe(viewLifecycleOwner)  {
           myAdapter.submitList(it)
       }
    }


    override fun onClickViewOnItem(historyDay: HistoryDay) {
        viewModel.deleteHistoryDay(historyDay)
    }

    override fun onClickViewOnItemAll(historyDay: HistoryDay) {
        Log.e("Teg", "${viewModel.hashCode()}")
        viewModel.fullDayInformation.value = historyDay

        val navController = findNavController()
        navController.navigate(R.id.detailsHistoryFragment)
    }
}