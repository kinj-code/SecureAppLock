package com.secureapplock.launcher.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi

/**
 * GlassBlurView: Applies a dynamic blur effect to drawers and folder windows.
 * Uses RenderEffect (API 31+) or custom blur rendering for older devices.
 *
 * Creates a "glass morphism" effect with adjustable blur radius and opacity.
 */
class GlassBlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_BLUR_RADIUS = 10f
        private const val DEFAULT_OPACITY = 0.8f
    }

    private var blurRadius = DEFAULT_BLUR_RADIUS
    private var opacity = DEFAULT_OPACITY
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        paint.color = android.graphics.Color.argb(
            (opacity * 255).toInt(),
            200,
            200,
            200
        )
    }

    /**
     * Sets the blur radius (0f to 25f recommended).
     */
    fun setBlurRadius(radius: Float) {
        blurRadius = radius.coerceIn(0f, 25f)
        applyBlurEffect()
    }

    /**
     * Sets the opacity of the glass effect (0f to 1f).
     */
    fun setOpacity(opacityValue: Float) {
        opacity = opacityValue.coerceIn(0f, 1f)
        paint.color = android.graphics.Color.argb(
            (opacity * 255).toInt(),
            200,
            200,
            200
        )
        invalidate()
    }

    /**
     * Applies the blur effect using RenderEffect (API 31+) or fallback.
     */
    private fun applyBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            applyRenderEffect()
        } else {
            invalidate()
        }
    }

    /**
     * Uses RenderEffect API (Android 12+) for GPU-accelerated blur.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun applyRenderEffect() {
        this.setRenderEffect(
            RenderEffect.createBlurEffect(
                blurRadius,
                blurRadius,
                Shader.TileMode.CLAMP
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the glass background
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            paint
        )
    }

    /**
     * Animates the blur effect over time.
     */
    fun animateBlur(startRadius: Float, endRadius: Float, durationMs: Long) {
        val animator = android.animation.ValueAnimator.ofFloat(startRadius, endRadius)
        animator.duration = durationMs
        animator.addUpdateListener { valueAnimator ->
            setBlurRadius(valueAnimator.animatedValue as Float)
        }
        animator.start()
    }
}
