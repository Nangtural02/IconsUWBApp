package com.example.icons_uwb_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.icons_uwb_app.serial.SerialViewModel
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serialViewModel: SerialViewModel by viewModels()
        //enableEdgeToEdge()

        setContent {
            ICONS_UWB_APPTheme {
                ICONS_UWB_App(serialViewModel)
            }
        }
    }
}