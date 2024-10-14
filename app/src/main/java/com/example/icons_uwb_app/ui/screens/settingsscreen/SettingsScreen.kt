package com.example.icons_uwb_app.ui.screens.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.icons_uwb_app.serial.SerialViewModel

@Composable
fun SettingsScreen(serialViewModel: SerialViewModel) {
    val rawString = serialViewModel.nowBlockString
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        Text(
            text = rawString.value,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ICONS_UWB_APPTheme {
        SettingsScreen()
    }
}

 */