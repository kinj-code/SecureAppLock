package com.secureapplock.launcher.typography

import android.content.Context
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.FontPreference
import kotlinx.coroutines.flow.Flow

/**
 * FontManager: Manages font preferences and typography settings.
 * Allows users to customize fonts across the launcher.
 */
class FontManager(
    private val context: Context,
    private val database: LauncherDatabase
) {

    private val fontPreferenceDao = database.fontPreferenceDao()

    companion object {
        // Available font families
        const val FONT_POPPINS = "poppins"
        const val FONT_INTER = "inter"
        const val FONT_OPENSANS = "opensans"
        const val FONT_ROBOTO = "roboto"
        const val FONT_PLAYFAIR = "playfair"
        const val FONT_LATO = "lato"
        const val FONT_MONTSERRAT = "montserrat"
        const val FONT_RALEWAY = "raleway"
    }

    /**
     * Gets the current font preferences as a Flow.
     */
    fun getFontPreference(): Flow<FontPreference?> {
        return fontPreferenceDao.getFontPreference()
    }

    /**
     * Gets the current font preferences (suspend function).
     */
    suspend fun getFontPreferenceSuspend(): FontPreference? {
        return fontPreferenceDao.getFontPreferenceSuspend()
    }

    /**
     * Updates the display font family.
     */
    suspend fun setDisplayFont(fontFamily: String) {
        fontPreferenceDao.setDisplayFont(fontFamily)
    }

    /**
     * Updates the heading font family.
     */
    suspend fun setHeadingFont(fontFamily: String) {
        fontPreferenceDao.setHeadingFont(fontFamily)
    }

    /**
     * Updates the body font family.
     */
    suspend fun setBodyFont(fontFamily: String) {
        fontPreferenceDao.setBodyFont(fontFamily)
    }

    /**
     * Updates the body font size.
     */
    suspend fun setBodyFontSize(fontSize: Float) {
        fontPreferenceDao.setBodyFontSize(fontSize.coerceIn(10f, 30f))
    }

    /**
     * Updates the heading font size.
     */
    suspend fun setHeadingFontSize(fontSize: Float) {
        fontPreferenceDao.setHeadingFontSize(fontSize.coerceIn(14f, 32f))
    }

    /**
     * Updates the display font size.
     */
    suspend fun setDisplayFontSize(fontSize: Float) {
        fontPreferenceDao.setDisplayFontSize(fontSize.coerceIn(18f, 40f))
    }

    /**
     * Updates the line spacing (multiplier).
     */
    suspend fun setLineSpacing(spacing: Float) {
        fontPreferenceDao.setLineSpacing(spacing.coerceIn(1f, 2.5f))
    }

    /**
     * Updates the letter spacing.
     */
    suspend fun setLetterSpacing(spacing: Float) {
        fontPreferenceDao.setLetterSpacing(spacing.coerceIn(0f, 5f))
    }

    /**
     * Resets font preferences to defaults.
     */
    suspend fun resetToDefaults() {
        fontPreferenceDao.insertFontPreference(
            FontPreference(
                displayFontFamily = FONT_POPPINS,
                headingFontFamily = FONT_INTER,
                bodyFontFamily = FONT_INTER,
                bodyFontSize = 14f,
                headingFontSize = 18f,
                displayFontSize = 24f,
                lineSpacing = 1.5f,
                letterSpacing = 0f
            )
        )
    }

    /**
     * Initializes default font preferences if they don't exist.
     */
    suspend fun initializeDefaultFonts() {
        if (fontPreferenceDao.getFontPreferenceSuspend() == null) {
            fontPreferenceDao.insertFontPreference(FontPreference())
        }
    }
}
