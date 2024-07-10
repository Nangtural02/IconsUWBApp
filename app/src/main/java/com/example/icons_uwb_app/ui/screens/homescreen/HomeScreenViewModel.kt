package com.example.icons_uwb_app.ui.screens.homescreen

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val environments: List<UWBEnvironment> = emptyList(),
    var selectedID : Int = -1
)

class HomeScreenViewModel(private val uwbEnvironmentsRepository: UWBEnvironmentsRepository) : ViewModel() {
    val homeUiState: StateFlow<HomeUiState> = uwbEnvironmentsRepository.getAllEnvironmentStream().map{HomeUiState(it)}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun getSelectedEnvironment(): Flow<UWBEnvironment> {
        return if(homeUiState.value.selectedID != -1){
            uwbEnvironmentsRepository.getEnvironmentStream(homeUiState.value.selectedID)
        } else{
            flowOf(UWBEnvironment())
        }
    }

    fun setSelectedID(id : Int){
        homeUiState.value.selectedID = id
    }

    suspend fun deleteEnvironment(item: UWBEnvironment){
        uwbEnvironmentsRepository.deleteItem(item)
    }
}