package com.example.wegarb.presentation.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wegarb.R
import com.example.wegarb.data.database.initialization.MainDataBaseInitialization
import com.example.wegarb.databinding.FragmentDaysBinding
import com.example.wegarb.presentation.view.adapters.DaysAdapter
import com.example.wegarb.presentation.vm.MainViewModel


class DaysFragment : Fragment() {
    private lateinit var binding: FragmentDaysBinding
    private lateinit var myAdapter: DaysAdapter
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
        myAdapter = DaysAdapter()
        rcViewDays.adapter = myAdapter
    }

    private fun observerForRcViewAndDataRcView() {
       mainViewModel.allInfoModels.observe(viewLifecycleOwner){
           myAdapter.submitList(it)
       }
    }


    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}