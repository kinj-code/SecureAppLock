package com.secureapplock.launcher.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.secureapplock.launcher.R
import com.secureapplock.launcher.databinding.ActivityHomeScreenBinding
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.gesture.LauncherGestureDetector
import com.secureapplock.launcher.manager.HomeScreenLockManager
import com.secureapplock.launcher.manager.LayoutLockInterceptor
import com.secureapplock.security.BiometricAuthManager
import com.secureapplock.security.PinSecurityManager
import kotlinx.coroutines.launch

/**
 * HomeScreenActivity: Main launcher home screen.
 * Integrates gesture detection, layout locking, and 3D parallax effects.
 */
class HomeScreenActivity : AppCompatActivity(), LauncherGestureDetector.GestureListener {

    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var database: LauncherDatabase
    private lateinit var homeScreenLockManager: HomeScreenLockManager
    private lateinit var biometricManager: BiometricAuthManager
    private lateinit var pinSecurityManager: PinSecurityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeManagers()
        setupGestureDetection()
        setupLayoutLocking()
        observeLockState()
    }

    private fun initializeManagers() {
        database = LauncherDatabase.getInstance(this)
        biometricManager = BiometricAuthManager(this)
        pinSecurityManager = PinSecurityManager(com.secureapplock.security.KeystoreManager(this))
        homeScreenLockManager = HomeScreenLockManager(
            this,
            database,
            biometricManager,
            pinSecurityManager
        )
    }

    private fun setupGestureDetection() {
        // Attach gesture detector to the home screen grid
        binding.homeScreenGrid.setOnTouchListener(
            LauncherGestureDetector(this, this)
        )
    }

    private fun setupLayoutLocking() {
        // Attach layout lock interceptor to prevent drag-drop when locked
        binding.homeScreenGrid.setOnDragListener(
            LayoutLockInterceptor(this, database, homeScreenLockManager)
        )
    }

    private fun observeLockState() {
        lifecycleScope.launch {
            homeScreenLockManager.observeLockState().collect { isLocked ->
                updateLockUI(isLocked)
            }
        }
    }

    override fun onSingleTap(event: android.view.MotionEvent?) {
        // Single tap: Launch the tapped app
        Toast.makeText(this, "App Launched", Toast.LENGTH_SHORT).show()
    }

    override fun onDoubleTap(event: android.view.MotionEvent?) {
        // Double tap: Trigger widget action (NOT opening the app)
        Toast.makeText(this, "Widget Action Triggered", Toast.LENGTH_SHORT).show()
    }

    override fun onLongPress(event: android.view.MotionEvent?) {
        // Long press: Enter edit mode (if not locked)
        lifecycleScope.launch {
            val canEdit = homeScreenLockManager.interceptLongClickEvent()
            if (canEdit) {
                Toast.makeText(this@HomeScreenActivity, "Edit Mode", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HomeScreenActivity, "Home Screen Locked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLockUI(isLocked: Boolean) {
        val lockIcon = if (isLocked) "🔒" else "🔓"
        binding.lockStatusText.text = "$lockIcon Layout Lock: ${if (isLocked) "ON" else "OFF"}"
    }
}
