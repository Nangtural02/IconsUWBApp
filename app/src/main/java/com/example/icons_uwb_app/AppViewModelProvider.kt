package com.example.icons_uwb_app

import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.icons_uwb_app.serial.SerialViewModel
import com.example.icons_uwb_app.ui.screens.homescreen.EnvironmentEditViewModel
import com.example.icons_uwb_app.ui.screens.homescreen.HomeScreenViewModel

object AppViewModelProvider{
    val Factory = viewModelFactory {
        //Main
        initializer {
            MainSerialViewModel()
        }
        //Home
        initializer {
            HomeScreenViewModel(
                uwbApplication().container.uwbEnvironmentsRepository
            )
        }
        initializer {
            EnvironmentEditViewModel(
                uwbApplication().container.uwbEnvironmentsRepository
            )
        }
        //Location

        //Data
    }
}

fun CreationExtras.uwbApplication(): UWBApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as UWBApplication)
