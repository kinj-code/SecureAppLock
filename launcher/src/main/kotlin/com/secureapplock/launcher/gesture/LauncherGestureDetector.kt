package com.secureapplock.launcher.gesture

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * LauncherGestureDetector: Distinguishes between single tap (launch app) and double tap (widget trigger).
 *
 * **Problem Solved:**
 * - Single tap must open the app.
 * - Double tap must trigger a widget action WITHOUT opening the app.
 * - No "flash" or accidental app launch on double tap.
 *
 * **Solution:**
 * - Use a delayed single-tap handler.
 * - Intercept double-tap BEFORE the single-tap is processed.
 * - Cancel pending single-tap when double-tap is detected.
 */
class LauncherGestureDetector(
    context: Context,
    private val listener: GestureListener
) : View.OnTouchListener {

    companion object {
        private const val TAP_TIMEOUT_MS = 300L // Time to wait for second tap
    }

    private val gestureDetector = GestureDetector(context, InternalGestureListener())
    private var pendingSingleTapRunnable: Runnable? = null
    private var lastTapTime = 0L

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (v == null || event == null) return false

        // Always pass to GestureDetector for processing
        return gestureDetector.onTouchEvent(event)
    }

    /**
     * Internal gesture listener that handles tap detection.
     */
    private inner class InternalGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            lastTapTime = System.currentTimeMillis()

            // Delay the single-tap action to allow for double-tap detection
            pendingSingleTapRunnable = Runnable {
                listener.onSingleTap(e)
            }

            // Post the single-tap with a delay
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                pendingSingleTapRunnable!!,
                TAP_TIMEOUT_MS
            )

            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            // Cancel pending single-tap if it hasn't executed yet
            pendingSingleTapRunnable?.let {
                android.os.Handler(android.os.Looper.getMainLooper()).removeCallbacks(it)
            }
            pendingSingleTapRunnable = null

            // Trigger the double-tap widget action
            listener.onDoubleTap(e)

            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            // Cancel pending single-tap for long press as well
            pendingSingleTapRunnable?.let {
                android.os.Handler(android.os.Looper.getMainLooper()).removeCallbacks(it)
            }
            pendingSingleTapRunnable = null

            listener.onLongPress(e)
        }
    }

    /**
     * Interface for gesture callback.
     */
    interface GestureListener {
        /**
         * Called when a single tap is detected (after TAP_TIMEOUT_MS).
         * Safe to launch the app here.
         */
        fun onSingleTap(event: MotionEvent?)

        /**
         * Called when a double tap is detected.
         * App will NOT open. Only the widget action is triggered.
         */
        fun onDoubleTap(event: MotionEvent?)

        /**
         * Called on long press (for edit mode).
         */
        fun onLongPress(event: MotionEvent?)
    }
}

/**
 * Extension function to easily attach the gesture detector to a view.
 */
fun View.setLauncherGestureListener(context: Context, listener: LauncherGestureDetector.GestureListener) {
    this.setOnTouchListener(LauncherGestureDetector(context, listener))
}
