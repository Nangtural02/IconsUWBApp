package com.example.icons_uwb_app.data.environments

import kotlinx.coroutines.flow.Flow

interface UWBEnvironmentsRepository {
    suspend fun insert(item: UWBEnvironment)
    suspend fun update(item: UWBEnvironment)
    suspend fun deleteItem(item: UWBEnvironment)
    fun getEnvironmentStream(id: Int): Flow<UWBEnvironment>
    fun getAllEnvironmentStream(): Flow<List<UWBEnvironment>>
}