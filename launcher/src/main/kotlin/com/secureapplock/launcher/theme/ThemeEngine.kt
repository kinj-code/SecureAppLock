package com.secureapplock.launcher.theme

import android.content.Context
import android.graphics.Color
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.ThemeConfig
import kotlinx.coroutines.flow.Flow

/**
 * ThemeEngine: Comprehensive theme management system.
 * Supports built-in themes, custom themes, and real-time theme switching.
 */
class ThemeEngine(
    private val context: Context,
    private val database: LauncherDatabase
) {

    private val themeConfigDao = database.themeConfigDao()

    companion object {
        // Built-in theme IDs
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_AMOLED = "amoled"
        const val THEME_MATERIAL_YOU = "material_you"
    }

    /**
     * Gets all available themes (built-in + custom).
     */
    fun getAllThemes(): Flow<List<ThemeConfig>> {
        return themeConfigDao.getAllThemes()
    }

    /**
     * Gets all built-in themes.
     */
    fun getBuiltInThemes(): Flow<List<ThemeConfig>> {
        return themeConfigDao.getBuiltInThemes()
    }

    /**
     * Gets all custom user themes.
     */
    fun getCustomThemes(): Flow<List<ThemeConfig>> {
        return themeConfigDao.getCustomThemes()
    }

    /**
     * Gets a specific theme by ID.
     */
    suspend fun getThemeById(themeId: String): ThemeConfig? {
        return themeConfigDao.getThemeById(themeId)
    }

    /**
     * Creates a custom theme.
     */
    suspend fun createCustomTheme(
        themeId: String,
        displayName: String,
        primaryColor: String,
        primaryDarkColor: String,
        accentColor: String,
        backgroundColor: String,
        textColorPrimary: String,
        textColorSecondary: String,
        isDarkMode: Boolean = false
    ): Boolean {
        val existingTheme = themeConfigDao.getThemeById(themeId)
        if (existingTheme != null) {
            return false // Theme already exists
        }

        val theme = ThemeConfig(
            themeId = themeId,
            displayName = displayName,
            primaryColor = primaryColor,
            primaryDarkColor = primaryDarkColor,
            accentColor = accentColor,
            backgroundColor = backgroundColor,
            textColorPrimary = textColorPrimary,
            textColorSecondary = textColorSecondary,
            isDarkMode = isDarkMode,
            isCustom = true
        )
        themeConfigDao.insertTheme(theme)
        return true
    }

    /**
     * Updates a theme.
     */
    suspend fun updateTheme(theme: ThemeConfig) {
        themeConfigDao.updateTheme(theme)
    }

    /**
     * Deletes a custom theme.
     */
    suspend fun deleteTheme(themeId: String): Boolean {
        val theme = themeConfigDao.getThemeById(themeId) ?: return false
        if (!theme.isCustom) {
            return false // Cannot delete built-in themes
        }
        themeConfigDao.deleteTheme(theme)
        return true
    }

    /**
     * Applies a theme to the context (stores preference).
     */
    suspend fun applyTheme(themeId: String) {
        val theme = themeConfigDao.getThemeById(themeId) ?: return
        // Store current theme preference
        val prefs = context.getSharedPreferences("launcher_theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("current_theme_id", themeId).apply()
    }

    /**
     * Gets the current applied theme.
     */
    fun getCurrentThemeId(): String {
        val prefs = context.getSharedPreferences("launcher_theme_prefs", Context.MODE_PRIVATE)
        return prefs.getString("current_theme_id", THEME_LIGHT) ?: THEME_LIGHT
    }

    /**
     * Initializes built-in themes.
     */
    suspend fun initializeBuiltInThemes() {
        val builtInThemes = listOf(
            ThemeConfig(
                themeId = THEME_LIGHT,
                displayName = "Light",
                primaryColor = "#6200EE",
                primaryDarkColor = "#3700B3",
                accentColor = "#03DAC6",
                backgroundColor = "#FFFFFF",
                textColorPrimary = "#000000",
                textColorSecondary = "#666666",
                isDarkMode = false,
                isCustom = false
            ),
            ThemeConfig(
                themeId = THEME_DARK,
                displayName = "Dark",
                primaryColor = "#BB86FC",
                primaryDarkColor = "#6200EE",
                accentColor = "#03DAC6",
                backgroundColor = "#121212",
                textColorPrimary = "#FFFFFF",
                textColorSecondary = "#AAAAAA",
                isDarkMode = true,
                isCustom = false
            ),
            ThemeConfig(
                themeId = THEME_AMOLED,
                displayName = "AMOLED",
                primaryColor = "#BB86FC",
                primaryDarkColor = "#6200EE",
                accentColor = "#03DAC6",
                backgroundColor = "#000000",
                textColorPrimary = "#FFFFFF",
                textColorSecondary = "#999999",
                isDarkMode = true,
                isCustom = false
            ),
            ThemeConfig(
                themeId = THEME_MATERIAL_YOU,
                displayName = "Material You",
                primaryColor = "#E91E63",
                primaryDarkColor = "#C2185B",
                accentColor = "#FF4081",
                backgroundColor = "#F5F5F5",
                textColorPrimary = "#212121",
                textColorSecondary = "#757575",
                isDarkMode = false,
                isCustom = false
            )
        )

        builtInThemes.forEach { theme ->
            if (themeConfigDao.getThemeById(theme.themeId) == null) {
                themeConfigDao.insertTheme(theme)
            }
        }
    }
}
