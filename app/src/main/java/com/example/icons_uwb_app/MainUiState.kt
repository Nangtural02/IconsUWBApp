package com.example.icons_uwb_app

import android.net.Uri
import com.example.icons_uwb_app.data.environments.Anchor

data class MainUiState(
    val connectedEnvironmentID : Int = -1,
    val connectedEnvironmentName: String = "연결된 시스템 없음",
    val connectedEnvironmentImage: Uri? = null,
    val connectedEnvironmentAnchors: List<Anchor> = emptyList(),
    var toggleDistanceCircle: Boolean = false,
    var toggleGrid: Boolean = true,
    var toggleAxis: Boolean = true
)
