package com.secureapplock.launcher.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.secureapplock.launcher.R
import com.secureapplock.launcher.databinding.ActivityHomeScreenExtendedBinding
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.gesture.GestureCustomizationManager
import com.secureapplock.launcher.gesture.LauncherGestureDetector
import com.secureapplock.launcher.paging.MultiScreenPagingManager
import com.secureapplock.launcher.theme.ThemeEngine
import com.secureapplock.launcher.widget.CustomWidgetManager
import com.secureapplock.launcher.animation.AnimationLibrary
import com.secureapplock.launcher.manager.HomeScreenLockManager
import com.secureapplock.launcher.manager.LayoutLockInterceptor
import com.secureapplock.security.BiometricAuthManager
import com.secureapplock.security.KeystoreManager
import com.secureapplock.security.PinSecurityManager
import kotlinx.coroutines.launch

/**
 * HomeScreenActivity (Extended): Integrates all Phase 1-3+ features.
 * - Multi-screen paging
 * - Custom gestures
 * - Widget customization
 * - Theme engine
 * - Animation library
 */
class HomeScreenActivityExtended : AppCompatActivity(), LauncherGestureDetector.GestureListener {

    private lateinit var binding: ActivityHomeScreenExtendedBinding
    private lateinit var database: LauncherDatabase
    private lateinit var homeScreenLockManager: HomeScreenLockManager
    private lateinit var multiScreenPagingManager: MultiScreenPagingManager
    private lateinit var gestureCustomizationManager: GestureCustomizationManager
    private lateinit var customWidgetManager: CustomWidgetManager
    private lateinit var themeEngine: ThemeEngine
    private lateinit var animationLibrary: AnimationLibrary
    private lateinit var biometricManager: BiometricAuthManager
    private lateinit var pinSecurityManager: PinSecurityManager
    private lateinit var keystoreManager: KeystoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenExtendedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeManagers()
        setupMultiScreenPaging()
        setupGestureDetection()
        setupThemeEngine()
        setupCustomWidgets()
        setupAnimations()
        setupLayoutLocking()
    }

    private fun initializeManagers() {
        keystoreManager = KeystoreManager(this)
        database = LauncherDatabase.getInstance(this)
        biometricManager = BiometricAuthManager(this)
        pinSecurityManager = PinSecurityManager(keystoreManager)
        homeScreenLockManager = HomeScreenLockManager(
            this,
            database,
            biometricManager,
            pinSecurityManager
        )
        multiScreenPagingManager = MultiScreenPagingManager(
            this,
            database,
            binding.viewPager
        )
        gestureCustomizationManager = GestureCustomizationManager(this, database)
        customWidgetManager = CustomWidgetManager(this, database)
        themeEngine = ThemeEngine(this, database)
        animationLibrary = AnimationLibrary(this, database)
    }

    private fun setupMultiScreenPaging() {
        lifecycleScope.launch {
            multiScreenPagingManager.initialize()
            multiScreenPagingManager.getAllPages().collect { pages ->
                Toast.makeText(this@HomeScreenActivityExtended, "${pages.size} pages available", Toast.LENGTH_SHORT).show()
            }
        }

        // Setup page change listener
        multiScreenPagingManager.setupPageChangeListener { pageIndex ->
            Toast.makeText(this, "Page $pageIndex", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupGestureDetection() {
        lifecycleScope.launch {
            gestureCustomizationManager.initializeDefaultGestures()
            gestureCustomizationManager.getEnabledGestures().collect { gestures ->
                Toast.makeText(this@HomeScreenActivityExtended, "${gestures.size} gestures loaded", Toast.LENGTH_SHORT).show()
            }
        }

        binding.homeScreenGrid.setOnTouchListener(
            LauncherGestureDetector(this, this)
        )
    }

    private fun setupThemeEngine() {
        lifecycleScope.launch {
            themeEngine.initializeBuiltInThemes()
            themeEngine.getAllThemes().collect { themes ->
                Toast.makeText(this@HomeScreenActivityExtended, "${themes.size} themes available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCustomWidgets() {
        lifecycleScope.launch {
            customWidgetManager.initializeBuiltInWidgets()
            customWidgetManager.getAllWidgetTypes().collect { widgets ->
                Toast.makeText(this@HomeScreenActivityExtended, "${widgets.size} widget types available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAnimations() {
        lifecycleScope.launch {
            animationLibrary.initializeBuiltInAnimations()
            animationLibrary.getAllAnimations().collect { animations ->
                Toast.makeText(this@HomeScreenActivityExtended, "${animations.size} animations loaded", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLayoutLocking() {
        binding.homeScreenGrid.setOnDragListener(
            LayoutLockInterceptor(this, database, homeScreenLockManager)
        )
    }

    override fun onSingleTap(event: android.view.MotionEvent?) {
        Toast.makeText(this, "App Launched", Toast.LENGTH_SHORT).show()
    }

    override fun onDoubleTap(event: android.view.MotionEvent?) {
        Toast.makeText(this, "Widget Action Triggered", Toast.LENGTH_SHORT).show()
    }

    override fun onLongPress(event: android.view.MotionEvent?) {
        lifecycleScope.launch {
            val canEdit = homeScreenLockManager.interceptLongClickEvent()
            if (canEdit) {
                Toast.makeText(this@HomeScreenActivityExtended, "Edit Mode", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HomeScreenActivityExtended, "Home Screen Locked", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
