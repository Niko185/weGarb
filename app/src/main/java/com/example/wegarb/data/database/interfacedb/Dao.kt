package com.example.wegarb.data.database.interfacedb
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.wegarb.data.database.entity.InfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Insert
    suspend fun insertInfoModelInDataBase(infoModel: InfoModel)

    @Query (value = "SELECT * FROM info_entity")
    fun getAllInfoModels(): Flow<List<InfoModel>>

}