package com.example.wegarb.presentation.view.fragments.history

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.R

import com.example.wegarb.databinding.FragmentHistoryBinding
import com.example.wegarb.domain.models.history.HistoryDay
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        showHistoryDaysInRecyclerView()
    }

    private fun initRecyclerView() = with(binding) {
        historyAdapter = HistoryAdapter(this@HistoryFragment)
        rcViewHistoryDays.adapter = historyAdapter
    }

    private fun showHistoryDaysInRecyclerView() {
       historyViewModel.historyDayList.observe(viewLifecycleOwner)  {
           historyAdapter.submitList(it)
       }
    }

    override fun onClickDeleteOnItem(historyDay: HistoryDay) {
        historyViewModel.deleteHistoryDay(historyDay)
    }

    override fun onClickItem(historyDay: HistoryDay) {
        val bundle = bundleOf("historyDay" to historyDay)
        val navController = findNavController()
        navController.navigate(R.id.detailsHistoryFragment, bundle)
    }
}