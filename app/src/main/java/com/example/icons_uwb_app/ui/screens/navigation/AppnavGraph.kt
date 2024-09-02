package com.example.icons_uwb_app.ui.screens.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.icons_uwb_app.AppViewModelProvider
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.serial.SerialViewModel
import com.example.icons_uwb_app.ui.screens.datascreen.DataScreen
import com.example.icons_uwb_app.ui.screens.homescreen.EnvironmentEditViewModel
import com.example.icons_uwb_app.ui.screens.homescreen.HomeNavHost
import com.example.icons_uwb_app.ui.screens.homescreen.HomeScreenViewModel
import com.example.icons_uwb_app.ui.screens.locationscreen.LocationScreen
import com.example.icons_uwb_app.ui.screens.settingsscreen.SettingsScreen

@Composable
fun AppNavGraph(navController: NavHostController, mainViewModel : MainSerialViewModel, serialViewModel: SerialViewModel) {
    val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val environmentEditViewModel: EnvironmentEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
    NavHost(navController = navController, startDestination = BottomNavItem.Home.screenRoute) {
        composable(BottomNavItem.Home.screenRoute) {
            HomeNavHost(navController = rememberNavController(),mainViewModel, homeScreenViewModel, environmentEditViewModel, serialViewModel)
        }
        composable(BottomNavItem.Location.screenRoute) {
            LocationScreen(mainViewModel=mainViewModel, serialViewModel)
        }
        composable(BottomNavItem.Data.screenRoute) {
            DataScreen()
        }
        composable(BottomNavItem.Settings.screenRoute) {
            SettingsScreen()
        }
    }
}