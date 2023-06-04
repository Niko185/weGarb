package com.example.wegarb.presentation.view.fragments.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {
    /*val historyDayInfo = MutableLiveData<HistoryDay>()*/
    val historyDayList: LiveData<List<HistoryDay>> = historyRepository.getAllHistoryDays()

    fun deleteHistoryDay(historyDay: HistoryDay) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.deleteDayFromHistory(historyDay)
        }
    }




}