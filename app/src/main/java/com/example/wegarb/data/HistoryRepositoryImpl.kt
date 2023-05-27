package com.example.wegarb.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.wegarb.data.history.local.history.dao.HistoryDayDao
import com.example.wegarb.domain.HistoryRepository
import com.example.wegarb.domain.models.history.HistoryDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryRepositoryImpl(private val historyDayDao: HistoryDayDao): HistoryRepository {

    override suspend fun saveDayInHistoryDomain(historyDay: HistoryDay) {
       historyDayDao.insertDayInHistoryMain(historyDay.mapToEntity())
    }

    override suspend fun deleteDayFromHistory(historyDay: HistoryDay) {
       historyDayDao.deleteDayFromHistoryMain(historyDay.mapToEntity())
    }

   override fun getAllHistoryDaysDomain(): LiveData<List<HistoryDay>> {
        return historyDayDao.getAllHistoryDays().map {
            it.map {
                it.mapToDomain()
            }
        }
   }
}