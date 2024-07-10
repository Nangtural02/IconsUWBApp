package com.example.icons_uwb_app.ui.screens.homescreen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.icons_uwb_app.MainSerialViewModel
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.data.environments.UWBEnvironment

enum class HomeScreens(@StringRes val title:Int){
    Home(title = R.string.SelectEnvironment),
    Detail(title = R.string.EnvironmentDetail),
    Entry(title = R.string.AddNewEnvironment)
}
@Composable
fun HomeNavHost(
    navController: NavHostController,
    mainViewModel: MainSerialViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    environmentEditViewModel: EnvironmentEditViewModel
){
    val selectedEnvironmentState by homeScreenViewModel.getSelectedEnvironment().collectAsState(initial = UWBEnvironment(title = "Saddd"))

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = HomeScreens.valueOf(
        backStackEntry?.destination?.route ?: HomeScreens.Home.name
    )
    Scaffold(
        topBar = {
            HomeScreenTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = {navController.navigateUp()}
            )
        }
    ) { innerPadding ->
        NavHost(navController = navController,
            startDestination = HomeScreens.Home.name,
            modifier = Modifier.padding(innerPadding)) {
            composable(HomeScreens.Home.name) {
                HomeScreen(
                    navigateToEnvironmentDetail = { navController.navigate(HomeScreens.Detail.name) },
                    navigateToEnvironmentEntry = { navController.navigate(HomeScreens.Entry.name) },
                    mainViewModel = mainViewModel,
                    homeScreenViewModel = homeScreenViewModel,
                    environmentEditViewModel= environmentEditViewModel
                )
            }
            composable(HomeScreens.Detail.name) {
                EnvironmentDetailScreen(
                    onNavigateUp = { navController.navigateUp() },
                    //homeScreenViewModel = homeScreenViewModel,
                    selectedEnvironment = selectedEnvironmentState,
                    environmentEditViewModel = environmentEditViewModel
                )
            }
            composable(HomeScreens.Entry.name) {
                EnvironmentEntryScreen(
                    onNavigateUp = { navController.navigateUp() },
                    canNavigateBack = navController.previousBackStackEntry != null,
                    environmentEditViewModel = environmentEditViewModel
                )
            }
        }
    }
}

