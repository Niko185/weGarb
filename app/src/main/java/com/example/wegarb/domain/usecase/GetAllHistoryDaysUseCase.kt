package com.example.wegarb.domain.usecase

import androidx.lifecycle.LiveData
import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.repository.HistoryRepository
import javax.inject.Inject

class GetAllHistoryDaysUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    fun execute(): LiveData<List<HistoryDay>> {
        return historyRepository.getAllHistoryDays()
    }
}