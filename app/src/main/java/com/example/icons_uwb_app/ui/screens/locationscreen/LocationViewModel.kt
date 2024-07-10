package com.example.icons_uwb_app.ui.screens.locationscreen

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository
import com.example.icons_uwb_app.ui.screens.homescreen.HomeUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LocationViewModel() {

    private val _locationUiState: MutableStateFlow<LocationUiState> = MutableStateFlow(LocationUiState())
    val locationUiState = _locationUiState.asStateFlow()
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }


}

data class LocationUiState(
    val tagX : Float = 0f,
    val tagY : Float = 0f,
    val tagZ : Float = 0f
)