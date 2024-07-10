package com.example.icons_uwb_app.data.rainging

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import kotlinx.coroutines.flow.Flow
import kotlin.time.TimeSource

@Dao
interface RangingDataDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: RangingData)
    @Update
    suspend fun update(item: RangingData)
    @Delete
    suspend fun delete(item: RangingData)
    @Query("SELECT * from RangingData where EnvironmentID = :id")
    fun getAllDataFromEnvironment(id: Int): Flow<RangingData>
    @Query("SELECT * from RangingData where EnvironmentID = :id AND createdTime = :time")
    fun getDataFromEnvironment(id : Int, time : TimeSource): Flow<List<RangingData>>
}