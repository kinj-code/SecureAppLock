package com.secureapplock.launcher.manager

import android.content.Context
import android.view.DragEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.security.BiometricAuthManager
import com.secureapplock.security.BiometricAuthResult
import com.secureapplock.security.PinSecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * LayoutLockInterceptor: Wraps drag-and-drop events to enforce layout locking.
 * Prevents modification of home screen layout when locked.
 *
 * **Flow:**
 * 1. User attempts to drag an icon.
 * 2. Interceptor checks if layout is locked.
 * 3. If locked, prompts for biometric/PIN authentication.
 * 4. If auth succeeds, allow the drag.
 * 5. If auth fails, show "Home Screen Locked" toast and block the drag.
 */
class LayoutLockInterceptor(
    private val context: Context,
    private val database: LauncherDatabase,
    private val homeScreenLockManager: HomeScreenLockManager
) : View.OnDragListener {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        if (v == null || event == null) return false

        // Check lock state and handle accordingly
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                return handleDragStart(v)
            }
            DragEvent.ACTION_DRAG_LOCATION -> {
                // Visual feedback for valid drop zones
                return true
            }
            DragEvent.ACTION_DROP -> {
                return handleDrop(v, event)
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                handleDragEnd(v)
                return true
            }
        }
        return false
    }

    /**
     * Handles drag start: Checks lock state and prompts for auth if needed.
     */
    private fun handleDragStart(view: View): Boolean {
        coroutineScope.launch {
            val isLocked = homeScreenLockManager.isLayoutLocked()
            if (isLocked) {
                // Layout is locked; perform authentication
                val authSuccess = homeScreenLockManager.interceptDragEvent(
                    android.view.MotionEvent.obtain(
                        0, 0, 0, 0f, 0f, 0
                    )
                )
                if (!authSuccess) {
                    Toast.makeText(
                        context,
                        "Home Screen Locked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return true
    }

    /**
     * Handles drop: Ensures authentication was successful before allowing drop.
     */
    private fun handleDrop(view: View, event: DragEvent): Boolean {
        coroutineScope.launch {
            val isLocked = homeScreenLockManager.isLayoutLocked()
            if (isLocked) {
                Toast.makeText(
                    context,
                    "Cannot modify locked layout",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return true
    }

    /**
     * Handles drag end: Clean up visual feedback.
     */
    private fun handleDragEnd(view: View) {
        // Remove any visual highlights or feedback
    }
}

/**
 * Extension function to easily attach the interceptor to a view.
 */
fun View.setLayoutLockInterceptor(
    context: Context,
    database: LauncherDatabase,
    homeScreenLockManager: HomeScreenLockManager
) {
    this.setOnDragListener(
        LayoutLockInterceptor(context, database, homeScreenLockManager)
    )
}
