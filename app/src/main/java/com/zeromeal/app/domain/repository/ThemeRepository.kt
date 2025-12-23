package com.zeromeal.app.domain.repository

import kotlinx.coroutines.flow.Flow

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

interface ThemeRepository {
    val themeMode: Flow<ThemeMode>
    fun setThemeMode(mode: ThemeMode)
}
