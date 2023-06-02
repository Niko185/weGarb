package com.example.wegarb.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.wegarb.data.history.local.history.dao.HistoryDayDao
import com.example.wegarb.domain.repository.HistoryRepository
import com.example.wegarb.domain.models.history.HistoryDay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(private val historyDayDao: HistoryDayDao): HistoryRepository {

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