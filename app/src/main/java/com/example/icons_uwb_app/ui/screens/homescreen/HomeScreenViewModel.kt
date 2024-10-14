package com.example.icons_uwb_app.ui.screens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

data class HomeUiState(
    val environments: List<UWBEnvironment> = emptyList(),
    var selectedID : Int = -1
)

class HomeScreenViewModel(private val uwbEnvironmentsRepository: UWBEnvironmentsRepository) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        // 초기 상태 업데이트
        updateHomeUiState()
    }
    fun updateHomeUiState(){
        viewModelScope.launch {
            uwbEnvironmentsRepository.getAllEnvironmentStream()
                .collect { environments ->
                    _homeUiState.value = HomeUiState(environments)
                }
        }

    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun getSelectedEnvironment(): Flow<UWBEnvironment> {
        return if (_homeUiState.value.selectedID != -1) {
            uwbEnvironmentsRepository.getEnvironmentStream(_homeUiState.value.selectedID)
        } else {
            flowOf(UWBEnvironment())
        }
    }

    fun setSelectedID(id: Int) {
        _homeUiState.value = _homeUiState.value.copy(selectedID = id) // 상태를 업데이트
    }

    suspend fun deleteEnvironment(item: UWBEnvironment) {
        uwbEnvironmentsRepository.deleteItem(item)
        // 삭제 후 상태 업데이트
        val updatedList = uwbEnvironmentsRepository.getAllEnvironmentStream().first()
        _homeUiState.value = _homeUiState.value.copy(environments = updatedList)
    }
}