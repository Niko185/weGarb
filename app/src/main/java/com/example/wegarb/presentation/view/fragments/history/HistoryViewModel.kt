package com.example.wegarb.presentation.view.fragments.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.repository.HistoryRepository
import com.example.wegarb.domain.usecase.DeleteHistoryDayUseCase
import com.example.wegarb.domain.usecase.GetAllHistoryDaysUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
   // private val historyRepository: HistoryRepository,
    private val deleteHistoryDayUseCase: DeleteHistoryDayUseCase,
    private val getAllHistoryDaysUseCase: GetAllHistoryDaysUseCase
) : ViewModel() {
    val historyDayList: LiveData<List<HistoryDay>> = getAllHistoryDaysUseCase.execute()//historyRepository.getAllHistoryDays()

    fun deleteHistoryDay(historyDay: HistoryDay) {
        viewModelScope.launch(Dispatchers.IO) {
            //historyRepository.deleteDayFromHistory(historyDay)
            deleteHistoryDayUseCase.execute(historyDay)
        }
    }

}