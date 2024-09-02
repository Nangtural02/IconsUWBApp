package com.example.icons_uwb_app

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme
import com.hoho.android.usbserial.*
import android.bluetooth.*
import androidx.activity.viewModels
import com.example.icons_uwb_app.serial.SerialViewModel

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