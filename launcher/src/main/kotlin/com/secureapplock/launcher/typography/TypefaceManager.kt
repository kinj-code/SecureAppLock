package com.secureapplock.launcher.typography

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.secureapplock.launcher.R

/**
 * TypefaceManager: Manages custom fonts and typography for the launcher.
 * Provides easy access to different font styles for titles, bodies, and UI elements.
 */
class TypefaceManager(private val context: Context) {

    companion object {
        // Font family IDs
        const val FONT_FAMILY_DISPLAY = "display"
        const val FONT_FAMILY_HEADING = "heading"
        const val FONT_FAMILY_BODY = "body"
        const val FONT_FAMILY_MONOSPACE = "monospace"
        const val FONT_FAMILY_DECORATIVE = "decorative"
    }

    /**
     * Gets the typeface for display titles (large, bold headlines).
     */
    fun getDisplayTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.poppins_bold)
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }
    }

    /**
     * Gets the typeface for section headings.
     */
    fun getHeadingTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.inter_semibold)
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }
    }

    /**
     * Gets the typeface for body text.
     */
    fun getBodyTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.inter_regular)
        } catch (e: Exception) {
            Typeface.DEFAULT
        }
    }

    /**
     * Gets the typeface for monospace text (code, numbers).
     */
    fun getMonospaceTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.roboto_mono_regular)
        } catch (e: Exception) {
            Typeface.MONOSPACE
        }
    }

    /**
     * Gets the typeface for decorative text (special headers).
     */
    fun getDecorativeTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.playfair_display_bold)
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }
    }

    /**
     * Gets the typeface for light/thin text.
     */
    fun getLightTypeface(): Typeface? {
        return try {
            ResourcesCompat.getFont(context, R.font.inter_light)
        } catch (e: Exception) {
            Typeface.DEFAULT
        }
    }

    /**
     * Gets a typeface by font family name.
     */
    fun getTypefaceByFamily(fontFamily: String): Typeface? {
        return when (fontFamily) {
            FONT_FAMILY_DISPLAY -> getDisplayTypeface()
            FONT_FAMILY_HEADING -> getHeadingTypeface()
            FONT_FAMILY_BODY -> getBodyTypeface()
            FONT_FAMILY_MONOSPACE -> getMonospaceTypeface()
            FONT_FAMILY_DECORATIVE -> getDecorativeTypeface()
            else -> Typeface.DEFAULT
        }
    }
}
