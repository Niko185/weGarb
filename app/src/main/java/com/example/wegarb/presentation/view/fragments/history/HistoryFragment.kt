package com.example.wegarb.presentation.view.fragments.history

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.data.storage.local.history.dto.HistoryDayDto
import com.example.wegarb.HistoryDayApp
import com.example.wegarb.databinding.FragmentDaysBinding
import com.example.wegarb.presentation.view.fragments.details.DetailsHistoryFragment
import com.example.wegarb.presentation.vm.MainViewModel
import com.example.wegarb.project_utils.FragmentManager


class HistoryFragment : Fragment(), HistoryAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var myAdapter: HistoryAdapter
    private val mainViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory((requireContext().applicationContext as HistoryDayApp).appDatabaseInitialization)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
       mainViewModel.getAllFullDaysInformation.observe(viewLifecycleOwner) {
           myAdapter.submitList(it)
       }
    }


    override fun onClickViewOnItem(historyDayDto: HistoryDayDto) {
        mainViewModel.deleteFullDayInformation(historyDayDto)
    }

    override fun onClickViewOnItemAll(historyDayDto: HistoryDayDto) {
        mainViewModel.savedFullDaysInformation.value = historyDayDto
        FragmentManager.setFragment(DetailsHistoryFragment.newInstance(), activity as AppCompatActivity)
    }


    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }



}