package com.example.wegarb.presentation.view.fragments.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsHistoryViewModel @Inject constructor(
   private val historyRepository: HistoryRepository
): ViewModel() {


}