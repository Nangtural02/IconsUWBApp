package com.example.icons_uwb_app.data

import android.content.Context
import com.example.icons_uwb_app.data.environments.EnvironmentDatabase
import com.example.icons_uwb_app.data.environments.OfflineUWBEnvironmentsRepository
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository

interface AppContainer{
    val uwbEnvironmentsRepository: UWBEnvironmentsRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    override val uwbEnvironmentsRepository: UWBEnvironmentsRepository by lazy{
        OfflineUWBEnvironmentsRepository(EnvironmentDatabase.getDatabase(context).environmentDao())
    }

}