package com.secureapplock.launcher.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * LayoutLockState: Tracks if home screen layout is locked.
 */
@Entity(tableName = "layout_lock_state")
data class LayoutLockState(
    @PrimaryKey
    val id: Int = 1,
    val isLocked: Boolean = false,
    val lockMethod: String = "biometric", // "biometric" or "pin"
    val lastModified: Long = System.currentTimeMillis()
)

/**
 * WidgetAssignment: Maps an app to a widget action (single tap = open, double tap = widget).
 */
@Entity(
    tableName = "widget_assignments",
    primaryKeys = ["appPackageName"]
)
data class WidgetAssignment(
    val appPackageName: String,
    val widgetId: Int, // Unique identifier for the widget action
    val widgetType: String, // e.g., "toggle_flashlight", "play_music", "open_url"
    val widgetData: String = "", // JSON payload for widget-specific config
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * AppGridPosition: Stores the home screen grid position of apps.
 */
@Entity(tableName = "app_grid_positions")
data class AppGridPosition(
    @PrimaryKey
    val appPackageName: String,
    val row: Int,
    val column: Int,
    val screenIndex: Int = 0, // For multi-screen support
    val lastUpdated: Long = System.currentTimeMillis()
)

/**
 * Type converters for Room.
 */
object Converters {
    fun fromDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    fun dateToLong(date: Date?): Long? {
        return date?.time
    }
}
