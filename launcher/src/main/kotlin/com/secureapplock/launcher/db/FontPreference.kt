package com.secureapplock.launcher.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * FontPreference: Stores user's font preferences.
 */
@Entity(tableName = "font_preferences")
data class FontPreference(
    @PrimaryKey
    val id: Int = 1,
    val displayFontFamily: String = "poppins", // poppins, playfair, opensans, etc.
    val headingFontFamily: String = "inter",
    val bodyFontFamily: String = "inter",
    val bodyFontSize: Float = 14f,
    val headingFontSize: Float = 18f,
    val displayFontSize: Float = 24f,
    val lineSpacing: Float = 1.5f,
    val letterSpacing: Float = 0f,
    val enableTextShadow: Boolean = false,
    val textShadowColor: String = "#000000",
    val textShadowRadius: Float = 0f,
    val createdAt: Long = System.currentTimeMillis()
)
