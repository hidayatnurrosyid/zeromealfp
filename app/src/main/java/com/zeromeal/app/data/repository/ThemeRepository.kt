package com.zeromeal.app.data.repository

import android.content.Context
import com.zeromeal.app.domain.repository.ThemeMode
import com.zeromeal.app.domain.repository.ThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ThemeRepository {

    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    private val _themeMode = MutableStateFlow(getStoredTheme())
    override val themeMode: Flow<ThemeMode> = _themeMode.asStateFlow()

    override fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    private fun getStoredTheme(): ThemeMode {
        val stored = prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(stored ?: ThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }
}
