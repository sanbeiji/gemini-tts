package com.example.geminittsdialects.models

import kotlinx.serialization.Serializable

@Serializable
data class UserSettings(
    val apiKey: String = "",
    val themePreference: String = "system", // "system", "light", "dark"
    val useDynamicColor: Boolean = true
)
