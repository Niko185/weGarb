package com.example.wegarb.data.history.local.history.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wegarb.data.history.local.history.entity.HistoryDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDayDao {
    @Query (value = "SELECT * FROM history_day_table")
    fun getAllHistoryDays(): Flow<List<HistoryDayEntity>>

    @Insert
    suspend fun insertHistoryDay(historyDayEntity: HistoryDayEntity)

    @Delete
    suspend fun deleteHistoryDay(historyDayEntity: HistoryDayEntity)

}