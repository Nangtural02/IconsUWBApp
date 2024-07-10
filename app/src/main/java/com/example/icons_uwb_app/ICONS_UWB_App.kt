package com.example.icons_uwb_app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.icons_uwb_app.ui.screens.navigation.AppNavGraph
import com.example.icons_uwb_app.ui.screens.navigation.BottomAppBar
import com.example.icons_uwb_app.ui.screens.navigation.TopBar

@Composable
fun ICONS_UWB_App(mainViewModel: MainSerialViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    val mainUiState by mainViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar(navController = navController, mainUiState = mainUiState) },
        bottomBar = { BottomAppBar(navController = navController) }
    ) {
        Box(Modifier.padding(it)){
            AppNavGraph(navController = navController, mainViewModel = mainViewModel)
        }
    }
}