package com.example.wegarb.data.database.interfacedb
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.wegarb.data.database.entity.InfoModel
import com.example.wegarb.data.models.GarbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query (value = "SELECT * FROM info_entity")
    fun getAllInfoModels(): Flow<List<InfoModel>>

    @Insert
    suspend fun insertInfoModelInDataBase(infoModel: InfoModel)
    @Delete
    suspend fun deleteInfoModelFromDatabase(infoModel: InfoModel)

    @Insert
    suspend fun insertGarbModel(arrayGarbModel: MutableList<GarbModel>) = arrayGarbModel

}