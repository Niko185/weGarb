package com.example.wegarb.data.database.interfacedb
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wegarb.data.database.entity.FullDayInformation
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query (value = "SELECT * FROM info_entity")
    fun getAllFullDaysInformation(): Flow<List<FullDayInformation>>

    @Insert
    suspend fun insertFullDayInformation(fullDayInformation: FullDayInformation)

    @Delete
    suspend fun deleteFullDayInformation(fullDayInformation: FullDayInformation)

}