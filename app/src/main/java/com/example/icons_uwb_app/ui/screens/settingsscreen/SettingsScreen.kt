package com.example.icons_uwb_app.ui.screens.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.icons_uwb_app.R
import com.example.icons_uwb_app.ui.theme.ICONS_UWB_APPTheme

@Composable
fun SettingsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Green)
    ) {
        Text(
            text = stringResource(id = R.string.Settings),
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ICONS_UWB_APPTheme {
        SettingsScreen()
    }
}