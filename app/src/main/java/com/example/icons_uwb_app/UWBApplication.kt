package com.example.icons_uwb_app

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.icons_uwb_app.data.AppContainer
import com.example.icons_uwb_app.data.AppDataContainer

class UWBApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}