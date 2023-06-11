package com.example.wegarb.domain.usecase

import com.example.wegarb.domain.models.history.HistoryDay
import com.example.wegarb.domain.repository.HistoryRepository
import javax.inject.Inject

class SaveDayInHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend fun execute(historyDay: HistoryDay) {
        historyRepository.saveDayInHistory(historyDay)
    }
}