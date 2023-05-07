package com.example.wegarb.data.storage.local.history.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wegarb.data.storage.local.history.dto.HistoryDayDto
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDayDao {
    @Query (value = "SELECT * FROM info_entity")
    fun getAllFullDaysInformation(): Flow<List<HistoryDayDto>>

    @Insert
    suspend fun insertFullDayInformation(historyDayDto: HistoryDayDto)

    @Delete
    suspend fun deleteFullDayInformation(historyDayDto: HistoryDayDto)

}