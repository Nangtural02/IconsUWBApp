package com.example.icons_uwb_app.ui.screens.homescreen

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.icons_uwb_app.data.environments.Anchor
import com.example.icons_uwb_app.data.environments.UWBEnvironment
import com.example.icons_uwb_app.data.environments.UWBEnvironmentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

class EnvironmentEditViewModel (private val uwbEnvironmentsRepository: UWBEnvironmentsRepository): ViewModel() {
    private val _uiState: MutableStateFlow<UWBEnvironment> = MutableStateFlow(UWBEnvironment(title = "이름 입력"))
    val uiState: StateFlow<UWBEnvironment> = _uiState.asStateFlow()

    suspend fun deleteNowEnvironment(){
        uwbEnvironmentsRepository.deleteItem(uiState.value)
    }

    fun updateUiState(environment: UWBEnvironment){
        _uiState.update{
            environment
        }
    }
    fun updateUiState(newTitle: String){
        _uiState.update {
            currentState -> currentState.copy(title = newTitle)
        }
    }
    fun editAddAnchor(newAnchor: Anchor){
        _uiState.update{
            currentState -> currentState.copy(anchors = _uiState.value.anchors.plus(newAnchor))
        }
    }
    fun editDeleteAnchor(
        indexToRemove : Int
    //targetAnchor: Anchor
    ){
        val tempList = _uiState.value.anchors.filterIndexed {
            index, _ -> index != indexToRemove
        }


        updateUiState(
            _uiState.value.copy(
                anchors = tempList
            )
        )
    }

    fun editChangeImage(newImage: Uri?){
        _uiState.update{
                currentState -> currentState.copy(imageUri = newImage)
        }
    }
    suspend fun updateUiState(){
        _uiState.update{
            currentState -> currentState.copy(lastConnectedDate = SimpleDateFormat("YY-MM-dd hh:mm").format(Date()))
        }
        updateEnvironment()
        Log.d("update","update now")
    }

    private fun validateInput(input: UWBEnvironment = _uiState.asStateFlow().value): Boolean{
        return with(input){
            !((title=="이름 입력")&&imageUri == null && anchors.isEmpty())//아무 것도 건드리지 않았을 때 저장 X
        }
    }
    suspend fun saveEnvironment(){
        if(validateInput()){
            Log.d("Data","Save New Environment")
            uwbEnvironmentsRepository.insert(_uiState.asStateFlow().value)
            resetEnvironment()
        }
    }
    fun resetEnvironment(){
        _uiState.update{
            UWBEnvironment(title = "이름 입력")
        }
    }

    suspend fun updateEnvironment(){
        Log.d("Data", "Update Environment")
        uwbEnvironmentsRepository.update(_uiState.asStateFlow().value)
    }




}