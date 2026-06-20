package com.secureapplock.launcher.typography

import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.secureapplock.launcher.R

/**
 * Typography utility functions for easy font application.
 */

/**
 * Sets the display font (Poppins Bold) on a TextView.
 */
fun TextView.setDisplayFont() {
    typeface = ResourcesCompat.getFont(context, R.font.poppins_bold)
}

/**
 * Sets the heading font (Inter SemiBold) on a TextView.
 */
fun TextView.setHeadingFont() {
    typeface = ResourcesCompat.getFont(context, R.font.inter_semibold)
}

/**
 * Sets the body font (Inter Regular) on a TextView.
 */
fun TextView.setBodyFont() {
    typeface = ResourcesCompat.getFont(context, R.font.inter_regular)
}

/**
 * Sets the monospace font (Roboto Mono) on a TextView.
 */
fun TextView.setMonospaceFont() {
    typeface = ResourcesCompat.getFont(context, R.font.roboto_mono_regular)
}

/**
 * Sets the decorative font (Playfair Display) on a TextView.
 */
fun TextView.setDecorativeFont() {
    typeface = ResourcesCompat.getFont(context, R.font.playfair_display_bold)
}

/**
 * Sets the light font (Inter Light) on a TextView.
 */
fun TextView.setLightFont() {
    typeface = ResourcesCompat.getFont(context, R.font.inter_light)
}
