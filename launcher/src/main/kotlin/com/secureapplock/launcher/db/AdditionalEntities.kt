package com.secureapplock.launcher.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ScreenPage: Represents a home screen page for multi-screen support.
 */
@Entity(tableName = "screen_pages")
data class ScreenPage(
    @PrimaryKey
    val screenIndex: Int,
    val name: String = "Screen $screenIndex",
    val isVisible: Boolean = true,
    val wallpaperId: Int = -1, // Wallpaper resource ID (-1 = default)
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * CustomGesture: Maps custom gestures to actions.
 * E.g., Swipe up = Open app drawer, Double-tap = Toggle widget, etc.
 */
@Entity(
    tableName = "custom_gestures",
    primaryKeys = ["gestureId"]
)
data class CustomGesture(
    val gestureId: String, // "swipe_up", "swipe_down", "double_tap_home", etc.
    val actionType: String, // "open_app_drawer", "open_settings", "toggle_widget", etc.
    val actionData: String = "", // JSON payload (e.g., app package name)
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * WidgetType: Defines custom widget types and their configurations.
 */
@Entity(
    tableName = "widget_types",
    primaryKeys = ["widgetTypeId"]
)
data class WidgetType(
    val widgetTypeId: String, // "flashlight", "music_player", "timer", "quick_url", etc.
    val displayName: String,
    val description: String = "",
    val icon: String = "", // Resource name or URI
    val configSchema: String = "", // JSON schema for widget configuration
    val version: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * ThemeConfig: Stores theme engine settings.
 */
@Entity(
    tableName = "theme_configs",
    primaryKeys = ["themeId"]
)
data class ThemeConfig(
    val themeId: String, // "light", "dark", "amoled", "custom_1", etc.
    val displayName: String,
    val primaryColor: String, // Hex color
    val primaryDarkColor: String,
    val accentColor: String,
    val backgroundColor: String,
    val textColorPrimary: String,
    val textColorSecondary: String,
    val isDarkMode: Boolean = false,
    val isCustom: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * TransitionAnimation: Configures transition animations between screens.
 */
@Entity(
    tableName = "transition_animations",
    primaryKeys = ["animationId"]
)
data class TransitionAnimation(
    val animationId: String, // "slide", "fade", "zoom", "rotate", "custom", etc.
    val displayName: String,
    val durationMs: Long = 300,
    val interpolatorType: String = "ease_in_out", // "linear", "ease_in", "ease_out", "ease_in_out", etc.
    val dampingRatio: Float = 0.8f, // For spring animations
    val stiffness: Float = 100f, // For spring animations
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
