package com.secureapplock.launcher.animation

import android.content.Context
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.Interpolator
import androidx.core.view.animation.PathInterpolatorCompat
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.TransitionAnimation
import kotlinx.coroutines.flow.Flow

/**
 * AnimationLibrary: Provides comprehensive transition animations for the launcher.
 * Supports slide, fade, zoom, rotate, and spring animations with customizable parameters.
 */
class AnimationLibrary(
    private val context: Context,
    private val database: LauncherDatabase
) {

    private val transitionAnimationDao = database.transitionAnimationDao()

    companion object {
        // Animation types
        const val ANIM_SLIDE = "slide"
        const val ANIM_FADE = "fade"
        const val ANIM_ZOOM = "zoom"
        const val ANIM_ROTATE = "rotate"
        const val ANIM_SPRING = "spring"
        const val ANIM_CUSTOM = "custom"

        // Interpolator types
        const val INTERPOLATOR_LINEAR = "linear"
        const val INTERPOLATOR_EASE_IN = "ease_in"
        const val INTERPOLATOR_EASE_OUT = "ease_out"
        const val INTERPOLATOR_EASE_IN_OUT = "ease_in_out"
        const val INTERPOLATOR_BOUNCE = "bounce"
    }

    /**
     * Gets all enabled animations.
     */
    fun getEnabledAnimations(): Flow<List<TransitionAnimation>> {
        return transitionAnimationDao.getEnabledAnimations()
    }

    /**
     * Gets all animations.
     */
    fun getAllAnimations(): Flow<List<TransitionAnimation>> {
        return transitionAnimationDao.getAllAnimations()
    }

    /**
     * Gets a specific animation by ID.
     */
    suspend fun getAnimationById(animationId: String): TransitionAnimation? {
        return transitionAnimationDao.getAnimationById(animationId)
    }

    /**
     * Creates a new transition animation.
     */
    suspend fun createAnimation(
        animationId: String,
        displayName: String,
        durationMs: Long = 300,
        interpolatorType: String = INTERPOLATOR_EASE_IN_OUT,
        dampingRatio: Float = 0.8f,
        stiffness: Float = 100f
    ): Boolean {
        val existingAnimation = transitionAnimationDao.getAnimationById(animationId)
        if (existingAnimation != null) {
            return false // Animation already exists
        }

        val animation = TransitionAnimation(
            animationId = animationId,
            displayName = displayName,
            durationMs = durationMs,
            interpolatorType = interpolatorType,
            dampingRatio = dampingRatio,
            stiffness = stiffness,
            isEnabled = true
        )
        transitionAnimationDao.insertAnimation(animation)
        return true
    }

    /**
     * Updates an animation.
     */
    suspend fun updateAnimation(animation: TransitionAnimation) {
        transitionAnimationDao.updateAnimation(animation)
    }

    /**
     * Deletes an animation.
     */
    suspend fun deleteAnimation(animationId: String): Boolean {
        val animation = transitionAnimationDao.getAnimationById(animationId) ?: return false
        transitionAnimationDao.deleteAnimation(animation)
        return true
    }

    /**
     * Gets the appropriate interpolator for the given type.
     */
    fun getInterpolator(interpolatorType: String): Interpolator {
        return when (interpolatorType) {
            INTERPOLATOR_LINEAR -> LinearInterpolator()
            INTERPOLATOR_EASE_IN -> AccelerateInterpolator()
            INTERPOLATOR_EASE_OUT -> DecelerateInterpolator()
            INTERPOLATOR_EASE_IN_OUT -> AccelerateDecelerateInterpolator()
            INTERPOLATOR_BOUNCE -> BounceInterpolator()
            else -> LinearInterpolator()
        }
    }

    /**
     * Initializes built-in animations.
     */
    suspend fun initializeBuiltInAnimations() {
        val builtInAnimations = listOf(
            TransitionAnimation(
                animationId = ANIM_SLIDE,
                displayName = "Slide",
                durationMs = 300,
                interpolatorType = INTERPOLATOR_EASE_IN_OUT,
                isEnabled = true
            ),
            TransitionAnimation(
                animationId = ANIM_FADE,
                displayName = "Fade",
                durationMs = 200,
                interpolatorType = INTERPOLATOR_LINEAR,
                isEnabled = true
            ),
            TransitionAnimation(
                animationId = ANIM_ZOOM,
                displayName = "Zoom",
                durationMs = 350,
                interpolatorType = INTERPOLATOR_EASE_IN_OUT,
                isEnabled = true
            ),
            TransitionAnimation(
                animationId = ANIM_ROTATE,
                displayName = "Rotate",
                durationMs = 400,
                interpolatorType = INTERPOLATOR_EASE_IN_OUT,
                isEnabled = true
            ),
            TransitionAnimation(
                animationId = ANIM_SPRING,
                displayName = "Spring",
                durationMs = 500,
                interpolatorType = "spring",
                dampingRatio = 0.6f,
                stiffness = 150f,
                isEnabled = true
            )
        )

        builtInAnimations.forEach { animation ->
            if (transitionAnimationDao.getAnimationById(animation.animationId) == null) {
                transitionAnimationDao.insertAnimation(animation)
            }
        }
    }
}

/**
 * Custom BounceInterpolator for bounce effect.
 */
class BounceInterpolator : Interpolator {
    override fun getInterpolation(t: Float): Float {
        return when {
            t < 0.36363f -> 7.5625f * t * t
            t < 0.727272f -> 7.5625f * (t - 0.545454f) * (t - 0.545454f) + 0.75f
            t < 0.909090f -> 7.5625f * (t - 0.818181f) * (t - 0.818181f) + 0.9375f
            else -> 7.5625f * (t - 0.954545f) * (t - 0.954545f) + 0.984375f
        }
    }
}
