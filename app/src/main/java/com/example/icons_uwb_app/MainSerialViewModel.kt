package com.example.icons_uwb_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MainSerialViewModel(): ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun connectEnvironment(targetEnvironment: UWBEnvironment){
        _uiState.update{ currentState->
            currentState.copy(
                connectedEnvironmentID = targetEnvironment.id,
                connectedEnvironmentName = targetEnvironment.title,
                connectedEnvironmentImage = targetEnvironment.imagePainterID,
                connectedEnvironmentAnchors = targetEnvironment.anchors
            )
        }

    }
    fun disconnectEnvironment(){
        _uiState.update{ MainUiState() }

    }



}