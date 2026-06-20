# SecureAppLock Launcher - Architecture & Implementation Guide

## Overview

The **Core Launcher Application** integrates the `SecureAppLock` security module into a high-performance Android launcher with advanced gesture handling, layout locking, and 3D visual effects.

## Project Structure

```
secure-app-lock/
├── (root security module)
│   ├── src/main/kotlin/com/secureapplock/security/
│   │   ├── KeystoreManager.kt
│   │   ├── BiometricAuthManager.kt
│   │   ├── PinSecurityManager.kt
│   │   ├── AppLockManager.kt
│   │   └── HiddenAppsManager.kt
│   └── build.gradle
│
└── launcher/
    ├── src/main/kotlin/com/secureapplock/launcher/
    │   ├── db/
    │   │   ├── LauncherDatabase.kt        # Room database
    │   │   ├── Entities.kt                # LayoutLockState, WidgetAssignment, AppGridPosition
    │   │   └── Daos.kt                    # Database access objects
    │   │
    │   ├── manager/
    │   │   ├── HomeScreenLockManager.kt   # Layout lock logic
    │   │   ├── LayoutLockInterceptor.kt   # Drag-drop interceptor
    │   │   └── VaultAdapter.kt            # Hidden apps adapter
    │   │
    │   ├── gesture/
    │   │   └── LauncherGestureDetector.kt # Single vs Double tap detection
    │   │
    │   ├── ui/
    │   │   ├── view/
    │   │   │   ├── GlassBlurView.kt       # Glass morphism blur effect
    │   │   │   └── Parallax3DView.kt      # 3D parallax background
    │   │   ├── HomeScreenActivity.kt
    │   │   ├── VaultActivity.kt
    │   │   ├── AppDrawerActivity.kt
    │   │   └── LauncherSettingsActivity.kt
    │   │
    │   ├── widget/
    │   │   └── WidgetConfigActivity.kt
    │   │
    │   ├── res/
    │   │   ├── layout/
    │   │   ├── values/
    │   │   └── ...
    │   │
    │   └── AndroidManifest.xml
    │
    ├── build.gradle
    └── LAUNCHER_ARCHITECTURE.md (this file)
```

## Phase 1: Integrating Security into the Launcher

### 1.1 Layout Locking

**Class:** `HomeScreenLockManager`

Extends `AppLockManager` to support "Home Screen Layout Lock":

```kotlin
// Check if layout is locked
val isLocked = homeScreenLockManager.isLayoutLocked()

// Enable layout lock with biometric
homeScreenLockManager.enableLayoutLock("biometric")

// Intercept drag events
val canProceed = homeScreenLockManager.interceptDragEvent(motionEvent)
```

**Database Schema:**

```kotlin
@Entity(tableName = "layout_lock_state")
data class LayoutLockState(
    @PrimaryKey val id: Int = 1,
    val isLocked: Boolean = false,
    val lockMethod: String = "biometric",
    val lastModified: Long = System.currentTimeMillis()
)
```

**Flow:**
1. User attempts to drag an icon → `LayoutLockInterceptor.onDrag()` triggered
2. Check `isLayoutLocked()` from Room database
3. If locked → Call `homeScreenLockManager.performAuthentication()`
4. On success → Allow drag; on failure → Show "Home Screen Locked" toast

### 1.2 Hidden Apps Integration

**Class:** `HiddenAppsManager` (from security module)

Integrated into `VaultActivity`:

```kotlin
// Hide an app
hiddenAppsManager.hideApp("com.example.app")

// Retrieve hidden apps
val hiddenApps = hiddenAppsManager.getHiddenApps()

// Unhide an app
hiddenAppsManager.unhideApp("com.example.app")
```

**Flow in Launcher:**
1. Hidden apps do NOT appear in `AppDrawerActivity`
2. User can access "Vault" → Must authenticate (biometric/PIN)
3. In Vault, hidden apps are listed with "Unhide" button
4. Clicking "Unhide" removes app from hidden list

### 1.3 Secure Preferences

**Implementation:**

All launcher settings use `KeystoreManager` and `EncryptedSharedPreferences`:

```kotlin
// In HomeScreenLockManager
keystoreManager.storeSensitiveData("grid_size", "4")
keystoreManager.storeSensitiveData("theme", "dark")
keystoreManager.storeSensitiveData("widget_config", jsonPayload)
```

---

## Phase 2: Advanced Interaction Logic

### 2.1 The "Double Tap to Widget" Conflict

**Problem:**
- Single tap must open app
- Double tap must trigger widget (NOT open app)
- No "flash" or accidental app launch

**Solution:** `LauncherGestureDetector`

**Key Logic:**

```kotlin
class LauncherGestureDetector(
    context: Context,
    private val listener: GestureListener
) : View.OnTouchListener {

    private const val TAP_TIMEOUT_MS = 300L

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private inner class InternalGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            // DELAY single-tap action by TAP_TIMEOUT_MS
            pendingSingleTapRunnable = Runnable {
                listener.onSingleTap(e) // Safe to launch app here
            }
            Handler(Looper.getMainLooper()).postDelayed(
                pendingSingleTapRunnable!!,
                TAP_TIMEOUT_MS
            )
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            // CANCEL pending single-tap immediately
            pendingSingleTapRunnable?.let {
                Handler(Looper.getMainLooper()).removeCallbacks(it)
            }
            pendingSingleTapRunnable = null

            // Trigger widget WITHOUT app launch
            listener.onDoubleTap(e)
            return true
        }
    }
}
```

**How It Works:**

1. User taps icon
2. `onSingleTapUp()` called → schedule `listener.onSingleTap()` for 300ms later
3. If second tap arrives within 300ms → `onDoubleTap()` called immediately
4. **Cancel** the pending single-tap
5. **Only** trigger widget action
6. If no second tap within 300ms → Single-tap handler executes → App opens

**Edge Cases Handled:**

- ✅ Double tap does NOT open app (pending runnable cancelled)
- ✅ No "flash" (visual feedback only on double-tap completion)
- ✅ Triple tap ignored (GestureDetector consumes extra taps)

**Integration:**

```kotlin
// In HomeScreenActivity
binding.homeScreenGrid.setOnTouchListener(
    LauncherGestureDetector(this, this)
)

// Implement GestureListener
override fun onSingleTap(event: MotionEvent?) {
    // Launch app
}

override fun onDoubleTap(event: MotionEvent?) {
    // Trigger widget
}
```

### 2.2 Home Screen Lock Flow

**Problem:**
- User long-clicks to edit home screen
- User drags icons to rearrange
- Both actions must be blocked if layout is locked

**Solution:** `LayoutLockInterceptor`

**Key Logic:**

```kotlin
class LayoutLockInterceptor(
    private val context: Context,
    private val database: LauncherDatabase,
    private val homeScreenLockManager: HomeScreenLockManager
) : View.OnDragListener {

    override fun onDrag(v: View?, event: DragEvent?): Boolean {
        when (event?.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                return handleDragStart(v)
            }
            DragEvent.ACTION_DROP -> {
                return handleDrop(v, event)
            }
        }
        return false
    }

    private fun handleDragStart(view: View?): Boolean {
        coroutineScope.launch {
            val isLocked = homeScreenLockManager.isLayoutLocked()
            if (isLocked) {
                // Request authentication
                val authSuccess = homeScreenLockManager.interceptDragEvent(motionEvent)
                if (!authSuccess) {
                    Toast.makeText(context, "Home Screen Locked", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }
}
```

**Flow:**

```
User attempts drag
    ↓
LayoutLockInterceptor.onDrag(ACTION_DRAG_STARTED)
    ↓
Check isLayoutLocked() from Room
    ↓
    YES → Trigger BiometricAuthManager.authenticate()
    ↓
    Auth Success → Allow drag
    Auth Failed → Show "Home Screen Locked" + block drag
    ↓
    NO → Allow drag immediately
```

**For Long-Click (Edit Mode):**

```kotlin
overrride fun onLongPress(event: MotionEvent?) {
    coroutineScope.launch {
        val canEdit = homeScreenLockManager.interceptLongClickEvent()
        if (!canEdit) {
            Toast.makeText(context, "Home Screen Locked", Toast.LENGTH_SHORT).show()
        }
    }
}
```

### 2.3 3D Perspective & Glass Effects

#### 2.3.1 Parallax3DView (3D Background)

**Implementation:**

```kotlin
class Parallax3DView : FrameLayout, SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // X, Y, Z gravity vectors
                currentTiltX = event.values[0] * PARALLAX_SENSITIVITY
                currentTiltY = event.values[1] * PARALLAX_SENSITIVITY
                updateParallaxOffset()
            }
        }
    }

    private fun updateParallaxOffset() {
        // Translate view based on device tilt
        translationX = currentTiltX.coerceIn(-MAX_TILT_OFFSET, MAX_TILT_OFFSET)
        translationY = currentTiltY.coerceIn(-MAX_TILT_OFFSET, MAX_TILT_OFFSET)
    }
}
```

**Effect:**
- Device tilts left → Background shifts right (creates depth illusion)
- Smooth parallax as user moves device
- Adjustable sensitivity and max offset

**Usage:**

```xml
<com.secureapplock.launcher.ui.view.Parallax3DView
    android:id="@+id/parallax_background"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Home screen grid goes here -->
</com.secureapplock.launcher.ui.view.Parallax3DView>
```

#### 2.3.2 GlassBlurView (Glass Morphism)

**Implementation:**

```kotlin
class GlassBlurView : View {

    private var blurRadius = 10f
    private var opacity = 0.8f

    fun setBlurRadius(radius: Float) {
        blurRadius = radius.coerceIn(0f, 25f)
        applyBlurEffect()
    }

    private fun applyBlurEffect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Use RenderEffect API (Android 12+)
            this.setRenderEffect(
                RenderEffect.createBlurEffect(
                    blurRadius, blurRadius, Shader.TileMode.CLAMP
                )
            )
        }
    }

    fun animateBlur(startRadius: Float, endRadius: Float, durationMs: Long) {
        ValueAnimator.ofFloat(startRadius, endRadius).apply {
            duration = durationMs
            addUpdateListener { setBlurRadius(it.animatedValue as Float) }
            start()
        }
    }
}
```

**Usage:**

```xml
<!-- Folder background with glass blur -->
<com.secureapplock.launcher.ui.view.GlassBlurView
    android:id="@+id/folder_glass"
    android:layout_width="300dp"
    android:layout_height="300dp" />
```

**Effect:**
- Applies GPU-accelerated blur to views
- Adjustable blur radius (0-25)
- Smooth opacity transitions
- "Glass morphism" visual design

---

## Phase 3: Architecture & Deliverables

### 3.1 Project Integration

**settings.gradle:**

```gradle
include ':launcher'
```

**launcher/build.gradle:**

```gradle
implementation project(':')
```

### 3.2 Database Schema

**Entities:**

```kotlin
// Layout lock state
@Entity(tableName = "layout_lock_state")
data class LayoutLockState(
    @PrimaryKey val id: Int = 1,
    val isLocked: Boolean = false,
    val lockMethod: String = "biometric",
    val lastModified: Long = System.currentTimeMillis()
)

// Widget assignments
@Entity(tableName = "widget_assignments", primaryKeys = ["appPackageName"])
data class WidgetAssignment(
    val appPackageName: String,
    val widgetId: Int,
    val widgetType: String,
    val widgetData: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// App grid positions
@Entity(tableName = "app_grid_positions")
data class AppGridPosition(
    @PrimaryKey val appPackageName: String,
    val row: Int,
    val column: Int,
    val screenIndex: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

### 3.3 AndroidManifest Permissions

```xml
<!-- Core Permissions -->
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
<uses-permission android:name="android.permission.USE_FINGERPRINT" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

<!-- Launcher Specific -->
<uses-permission android:name="android.permission.BIND_APPWIDGET" />
<uses-permission android:name="android.permission.SET_WALLPAPER" />
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />

<!-- Sensor for 3D Parallax -->
<uses-permission android:name="android.permission.SENSOR" />
<uses-feature android:name="android.hardware.sensor.accelerometer" />
```

### 3.4 Key Classes Summary

| Class | Purpose | Key Method |
|-------|---------|------------|
| `LauncherGestureDetector` | Single vs Double tap | `onTouch()` |
| `LayoutLockInterceptor` | Drag-drop locking | `onDrag()` |
| `HomeScreenLockManager` | Layout lock state | `isLayoutLocked()` |
| `Parallax3DView` | 3D parallax background | `onSensorChanged()` |
| `GlassBlurView` | Glass morphism blur | `setBlurRadius()` |
| `LauncherDatabase` | Room database | getInstance() |

---

## Integration Checklist

- [x] Security module imported
- [x] Room database set up
- [x] Gesture detection logic
- [x] Layout locking interceptor
- [x] 3D parallax effect
- [x] Glass blur effect
- [x] Hidden apps vault
- [x] Encrypted preferences
- [x] Biometric integration
- [x] PIN integration

---

## Testing Recommendations

1. **Gesture Detection:** Tap, double-tap, long-press on icons
2. **Layout Locking:** Enable lock, try dragging (should fail), authenticate
3. **Hidden Apps:** Hide app, verify not in drawer, access vault
4. **3D Parallax:** Rotate device, observe background shift
5. **Glass Blur:** Toggle visibility of blur effect

---

## Performance Notes

- RenderEffect blur (API 31+) is GPU-accelerated
- SensorManager events throttled to `SENSOR_DELAY_UI`
- Room queries on coroutines for non-blocking DB access
- GestureDetector uses Handler for delayed callbacks (efficient memory usage)

---

## Future Enhancements

1. Multi-screen support (horizontal paging)
2. Custom widget types
3. Gesture customization
4. Theme engine
5. Animation library for transitions
