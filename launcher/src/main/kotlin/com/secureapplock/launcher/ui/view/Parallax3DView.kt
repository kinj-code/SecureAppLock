package com.secureapplock.launcher.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.view.SurfaceView
import android.widget.FrameLayout
import kotlin.math.abs

/**
 * Parallax3DView: Custom FrameLayout that applies a 3D parallax effect based on device rotation.
 * Uses SensorManager to detect accelerometer/gyroscope data and adjust the wallpaper perspective.
 *
 * **Effect:**
 * - As the device tilts, the background shifts to simulate depth.
 * - Creates a subtle 3D illusion on the home screen.
 */
class Parallax3DView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), SensorEventListener {

    companion object {
        private const val MAX_TILT_OFFSET = 20f // Maximum pixel offset
        private const val PARALLAX_SENSITIVITY = 0.5f
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private var currentTiltX = 0f
    private var currentTiltY = 0f
    private var offsetX = 0f
    private var offsetY = 0f

    private var isEnabled = true

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isEnabled && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        }
        if (isEnabled && gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || !isEnabled) return

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // Calculate tilt based on gravity vector
                currentTiltX = event.values[0] * PARALLAX_SENSITIVITY
                currentTiltY = event.values[1] * PARALLAX_SENSITIVITY
                updateParallaxOffset()
            }
            Sensor.TYPE_GYROSCOPE -> {
                // Optional: Use gyroscope for more precise rotation
                // event.values[0] = X rotation rate
                // event.values[1] = Y rotation rate
                // event.values[2] = Z rotation rate
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No action needed
    }

    /**
     * Updates the parallax offset based on current tilt.
     */
    private fun updateParallaxOffset() {
        offsetX = currentTiltX.coerceIn(-MAX_TILT_OFFSET, MAX_TILT_OFFSET)
        offsetY = currentTiltY.coerceIn(-MAX_TILT_OFFSET, MAX_TILT_OFFSET)

        // Apply the offset via translation
        translationX = offsetX
        translationY = offsetY
    }

    /**
     * Enables or disables the parallax effect.
     */
    fun setParallaxEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (enabled && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            sensorManager.unregisterListener(this)
        }
    }

    /**
     * Gets the current parallax offset as a pair (offsetX, offsetY).
     */
    fun getCurrentOffset(): Pair<Float, Float> = Pair(offsetX, offsetY)
}
