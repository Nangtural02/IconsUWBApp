package com.example.icons_uwb_app

import com.example.icons_uwb_app.data.environments.Anchor

data class MainUiState(
    val connectedEnvironmentID : Int = -1,
    val connectedEnvironmentName: String = "연결된 시스템 없음",
    val connectedEnvironmentImage: Int = -1,
    val connectedEnvironmentAnchors: List<Anchor> = emptyList()
)
