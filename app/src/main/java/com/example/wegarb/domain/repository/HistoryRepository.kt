package com.example.wegarb.domain.repository


import androidx.lifecycle.LiveData
import com.example.wegarb.domain.models.history.HistoryDay
import kotlinx.coroutines.flow.Flow


interface HistoryRepository {
    suspend fun saveDayInHistoryDomain(historyDay: HistoryDay)

    suspend fun deleteDayFromHistory(historyDay: HistoryDay)

    fun getAllHistoryDaysDomain(): LiveData<List<HistoryDay>>
}