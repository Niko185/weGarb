package com.example.wegarb.presentation.view.fragments.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wegarb.databinding.FragmentDetailsHistoryBinding
import com.example.wegarb.presentation.view.fragments.history.HistoryViewModel
import com.example.wegarb.presentation.view.fragments.weather.WeatherViewModel
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



}
