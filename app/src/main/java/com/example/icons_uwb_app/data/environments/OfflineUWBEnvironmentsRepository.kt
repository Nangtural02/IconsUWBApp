package com.example.icons_uwb_app.data.environments

import kotlinx.coroutines.flow.Flow

class OfflineUWBEnvironmentsRepository (private val uwbEnvironmentDao: UWBEnvironmentDao) :
    UWBEnvironmentsRepository {
    override suspend fun insert(item: UWBEnvironment) = uwbEnvironmentDao.insert(item)
    override suspend fun update(item: UWBEnvironment) = uwbEnvironmentDao.update(item)
    override suspend fun deleteItem(item: UWBEnvironment) = uwbEnvironmentDao.delete(item)
    override fun getEnvironmentStream(id: Int): Flow<UWBEnvironment> = uwbEnvironmentDao.getEnvironment(id)
    override fun getAllEnvironmentStream(): Flow<List<UWBEnvironment>> = uwbEnvironmentDao.getAllEnvironment()
}