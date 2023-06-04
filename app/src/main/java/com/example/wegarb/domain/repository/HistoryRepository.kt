package com.example.wegarb.domain.repository


import androidx.lifecycle.LiveData
import com.example.wegarb.domain.models.history.HistoryDay


interface HistoryRepository {
    suspend fun saveDayInHistory(historyDay: HistoryDay)

    suspend fun deleteDayFromHistory(historyDay: HistoryDay)

    fun getAllHistoryDays(): LiveData<List<HistoryDay>>
}