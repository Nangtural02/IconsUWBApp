package com.example.icons_uwb_app.data.environments

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UWBEnvironmentDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: UWBEnvironment)
    @Update
    suspend fun update(item: UWBEnvironment)
    @Delete
    suspend fun delete(item: UWBEnvironment)
    @Query("SELECT * from Environment where id = :id")
    fun getEnvironment(id: Int): Flow<UWBEnvironment>
    @Query("SELECT * from Environment")
    fun getAllEnvironment(): Flow<List<UWBEnvironment>>
}