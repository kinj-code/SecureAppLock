package com.secureapplock.launcher.gesture

import android.content.Context
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.CustomGesture
import kotlinx.coroutines.flow.Flow

/**
 * GestureCustomizationManager: Allows users to customize gesture actions.
 * Map gestures like swipe, double-tap, long-press to custom actions.
 */
class GestureCustomizationManager(
    private val context: Context,
    private val database: LauncherDatabase
) {

    private val customGestureDao = database.customGestureDao()

    companion object {
        // Predefined gesture IDs
        const val GESTURE_SWIPE_UP = "swipe_up"
        const val GESTURE_SWIPE_DOWN = "swipe_down"
        const val GESTURE_SWIPE_LEFT = "swipe_left"
        const val GESTURE_SWIPE_RIGHT = "swipe_right"
        const val GESTURE_DOUBLE_TAP_HOME = "double_tap_home"
        const val GESTURE_LONG_PRESS_HOME = "long_press_home"
        const val GESTURE_PINCH_IN = "pinch_in"
        const val GESTURE_PINCH_OUT = "pinch_out"

        // Action types
        const val ACTION_OPEN_APP_DRAWER = "open_app_drawer"
        const val ACTION_OPEN_SETTINGS = "open_settings"
        const val ACTION_OPEN_SEARCH = "open_search"
        const val ACTION_TOGGLE_WIDGET = "toggle_widget"
        const val ACTION_LAUNCH_APP = "launch_app"
        const val ACTION_OPEN_VAULT = "open_vault"
        const val ACTION_NOTIFICATION_PANEL = "notification_panel"
    }

    /**
     * Gets all enabled custom gestures.
     */
    fun getEnabledGestures(): Flow<List<CustomGesture>> {
        return customGestureDao.getEnabledGestures()
    }

    /**
     * Gets all custom gestures.
     */
    fun getAllGestures(): Flow<List<CustomGesture>> {
        return customGestureDao.getAllGestures()
    }

    /**
     * Gets a specific gesture by ID.
     */
    suspend fun getGestureById(gestureId: String): CustomGesture? {
        return customGestureDao.getGestureById(gestureId)
    }

    /**
     * Creates a new custom gesture.
     */
    suspend fun createGesture(
        gestureId: String,
        actionType: String,
        actionData: String = ""
    ): Boolean {
        val gesture = CustomGesture(
            gestureId = gestureId,
            actionType = actionType,
            actionData = actionData,
            isEnabled = true
        )
        customGestureDao.insertGesture(gesture)
        return true
    }

    /**
     * Updates a gesture.
     */
    suspend fun updateGesture(gesture: CustomGesture) {
        customGestureDao.updateGesture(gesture)
    }

    /**
     * Enables or disables a gesture.
     */
    suspend fun setGestureEnabled(gestureId: String, enabled: Boolean) {
        customGestureDao.setGestureEnabled(gestureId, enabled)
    }

    /**
     * Deletes a custom gesture.
     */
    suspend fun deleteGesture(gestureId: String) {
        val gesture = customGestureDao.getGestureById(gestureId) ?: return
        customGestureDao.deleteGesture(gesture)
    }

    /**
     * Initializes default gestures.
     */
    suspend fun initializeDefaultGestures() {
        val defaultGestures = listOf(
            CustomGesture(GESTURE_SWIPE_UP, ACTION_OPEN_APP_DRAWER),
            CustomGesture(GESTURE_SWIPE_DOWN, ACTION_NOTIFICATION_PANEL),
            CustomGesture(GESTURE_DOUBLE_TAP_HOME, ACTION_TOGGLE_WIDGET),
            CustomGesture(GESTURE_LONG_PRESS_HOME, ACTION_OPEN_SETTINGS)
        )

        defaultGestures.forEach { gesture ->
            if (customGestureDao.getGestureById(gesture.gestureId) == null) {
                customGestureDao.insertGesture(gesture)
            }
        }
    }
}
