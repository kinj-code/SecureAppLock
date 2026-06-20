package com.secureapplock.launcher.animation

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.secureapplock.launcher.db.TransitionAnimation

/**
 * TransitionAnimator: Applies various transition animations to views.
 * Supports slide, fade, zoom, rotate, and spring animations.
 */
class TransitionAnimator {

    companion object {
        /**
         * Applies a slide animation (horizontal).
         */
        fun slideHorizontal(
            view: View,
            fromX: Float,
            toX: Float,
            duration: Long = 300,
            onEnd: (() -> Unit)? = null
        ) {
            val animator = ObjectAnimator.ofFloat(view, "translationX", fromX, toX)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }

        /**
         * Applies a fade animation (opacity).
         */
        fun fadeInOut(
            view: View,
            fromAlpha: Float,
            toAlpha: Float,
            duration: Long = 200,
            onEnd: (() -> Unit)? = null
        ) {
            val animator = ObjectAnimator.ofFloat(view, "alpha", fromAlpha, toAlpha)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }

        /**
         * Applies a zoom animation (scale).
         */
        fun zoom(
            view: View,
            fromScale: Float,
            toScale: Float,
            duration: Long = 350,
            onEnd: (() -> Unit)? = null
        ) {
            val scaleX = PropertyValuesHolder.ofFloat("scaleX", fromScale, toScale)
            val scaleY = PropertyValuesHolder.ofFloat("scaleY", fromScale, toScale)
            val animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }

        /**
         * Applies a rotate animation.
         */
        fun rotate(
            view: View,
            fromDegrees: Float,
            toDegrees: Float,
            duration: Long = 400,
            onEnd: (() -> Unit)? = null
        ) {
            val animator = ObjectAnimator.ofFloat(view, "rotation", fromDegrees, toDegrees)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }

        /**
         * Applies a spring animation (physics-based).
         */
        fun spring(
            view: View,
            property: DynamicAnimation.ViewProperty,
            finalPosition: Float,
            dampingRatio: Float = 0.6f,
            stiffness: Float = 150f
        ) {
            val springAnimation = SpringAnimation(view, property, finalPosition)
            springAnimation.spring.dampingRatio = dampingRatio
            springAnimation.spring.stiffness = stiffness
            springAnimation.start()
        }

        /**
         * Applies a combined animation (slide + fade).
         */
        fun slideFade(
            view: View,
            fromX: Float,
            toX: Float,
            fromAlpha: Float,
            toAlpha: Float,
            duration: Long = 300,
            onEnd: (() -> Unit)? = null
        ) {
            val translationX = PropertyValuesHolder.ofFloat("translationX", fromX, toX)
            val alpha = PropertyValuesHolder.ofFloat("alpha", fromAlpha, toAlpha)
            val animator = ObjectAnimator.ofPropertyValuesHolder(view, translationX, alpha)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }

        /**
         * Applies a complex animation with multiple properties.
         */
        fun complexAnimation(
            view: View,
            properties: Map<String, Pair<Float, Float>>,
            duration: Long = 300,
            onEnd: (() -> Unit)? = null
        ) {
            val holders = properties.map { (propertyName, values) ->
                PropertyValuesHolder.ofFloat(propertyName, values.first, values.second)
            }.toTypedArray()

            val animator = ObjectAnimator.ofPropertyValuesHolder(view, *holders)
            animator.duration = duration
            animator.addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onEnd?.invoke()
                }
            })
            animator.start()
        }
    }
}
