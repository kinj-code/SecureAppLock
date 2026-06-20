package com.secureapplock.launcher.widget

import android.content.Context
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.WidgetType
import kotlinx.coroutines.flow.Flow

/**
 * CustomWidgetManager: Manages custom widget types and configurations.
 * Supports flashlight, music player, timer, quick URL, and extensible types.
 */
class CustomWidgetManager(
    private val context: Context,
    private val database: LauncherDatabase
) {

    private val widgetTypeDao = database.widgetTypeDao()

    companion object {
        // Built-in widget types
        const val WIDGET_FLASHLIGHT = "flashlight"
        const val WIDGET_MUSIC_PLAYER = "music_player"
        const val WIDGET_TIMER = "timer"
        const val WIDGET_QUICK_URL = "quick_url"
        const val WIDGET_WEATHER = "weather"
        const val WIDGET_CALENDAR = "calendar"
        const val WIDGET_NOTES = "notes"
        const val WIDGET_QUICK_SETTINGS = "quick_settings"
    }

    /**
     * Gets all available widget types.
     */
    fun getAllWidgetTypes(): Flow<List<WidgetType>> {
        return widgetTypeDao.getAllWidgetTypes()
    }

    /**
     * Gets a specific widget type.
     */
    suspend fun getWidgetType(widgetTypeId: String): WidgetType? {
        return widgetTypeDao.getWidgetTypeById(widgetTypeId)
    }

    /**
     * Registers a new custom widget type.
     */
    suspend fun registerWidgetType(
        widgetTypeId: String,
        displayName: String,
        description: String = "",
        configSchema: String = ""
    ): Boolean {
        val existingType = widgetTypeDao.getWidgetTypeById(widgetTypeId)
        if (existingType != null) {
            return false // Widget type already exists
        }

        val widgetType = WidgetType(
            widgetTypeId = widgetTypeId,
            displayName = displayName,
            description = description,
            configSchema = configSchema
        )
        widgetTypeDao.insertWidgetType(widgetType)
        return true
    }

    /**
     * Updates an existing widget type.
     */
    suspend fun updateWidgetType(widgetType: WidgetType) {
        widgetTypeDao.updateWidgetType(widgetType)
    }

    /**
     * Deletes a widget type.
     */
    suspend fun deleteWidgetType(widgetTypeId: String): Boolean {
        val widgetType = widgetTypeDao.getWidgetTypeById(widgetTypeId) ?: return false
        widgetTypeDao.deleteWidgetType(widgetType)
        return true
    }

    /**
     * Initializes built-in widget types.
     */
    suspend fun initializeBuiltInWidgets() {
        val builtInWidgets = listOf(
            WidgetType(
                widgetTypeId = WIDGET_FLASHLIGHT,
                displayName = "Flashlight",
                description = "Toggle flashlight on/off",
                configSchema = "{\"brightness\": 1.0}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_MUSIC_PLAYER,
                displayName = "Music Player",
                description = "Control music playback",
                configSchema = "{\"app\": \"com.spotify.music\"}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_TIMER,
                displayName = "Timer",
                description = "Quick timer widget",
                configSchema = "{\"duration\": 300}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_QUICK_URL,
                displayName = "Quick URL",
                description = "Open a quick URL",
                configSchema = "{\"url\": \"\"}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_WEATHER,
                displayName = "Weather",
                description = "Display current weather",
                configSchema = "{\"unit\": \"celsius\"}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_CALENDAR,
                displayName = "Calendar",
                description = "Show calendar",
                configSchema = "{\"view\": \"month\"}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_NOTES,
                displayName = "Notes",
                description = "Quick notes widget",
                configSchema = "{\"lines\": 3}"
            ),
            WidgetType(
                widgetTypeId = WIDGET_QUICK_SETTINGS,
                displayName = "Quick Settings",
                description = "Control system settings",
                configSchema = "{\"settings\": []}"
            )
        )

        builtInWidgets.forEach { widget ->
            if (widgetTypeDao.getWidgetTypeById(widget.widgetTypeId) == null) {
                widgetTypeDao.insertWidgetType(widget)
            }
        }
    }
}
