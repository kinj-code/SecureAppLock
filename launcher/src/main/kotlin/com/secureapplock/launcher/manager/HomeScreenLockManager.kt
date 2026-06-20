package com.secureapplock.launcher.manager

import android.content.Context
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.db.LayoutLockState
import com.secureapplock.security.BiometricAuthManager
import com.secureapplock.security.BiometricAuthResult
import com.secureapplock.security.PinSecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * HomeScreenLockManager: Intercepts drag, drop, and edit mode events.
 * Enforces biometric/PIN authentication before allowing layout modifications.
 */
class HomeScreenLockManager(
    private val context: Context,
    private val database: LauncherDatabase,
    private val biometricManager: BiometricAuthManager,
    private val pinSecurityManager: PinSecurityManager
) {

    private val layoutLockDao = database.layoutLockDao()
    private val lockStateFlow: Flow<LayoutLockState?> = layoutLockDao.getLayoutLockState()

    private val _authenticationResult = MutableLiveData<AuthenticationResult>()
    val authenticationResult: LiveData<AuthenticationResult> = _authenticationResult

    /**
     * Observes the lock state as a Flow.
     */
    fun observeLockState(): Flow<Boolean> {
        return lockStateFlow.map { it?.isLocked ?: false }
    }

    /**
     * Checks if layout is currently locked.
     */
    suspend fun isLayoutLocked(): Boolean {
        return layoutLockDao.isLayoutLocked()
    }

    /**
     * Enables home screen layout lock.
     */
    suspend fun enableLayoutLock(method: String = "biometric") {
        val state = LayoutLockState(isLocked = true, lockMethod = method)
        layoutLockDao.insertOrUpdateLockState(state)
    }

    /**
     * Disables home screen layout lock.
     */
    suspend fun disableLayoutLock() {
        layoutLockDao.setLayoutLocked(false)
    }

    /**
     * Intercepts drag events and enforces authentication if locked.
     * Returns true if the event should be allowed to proceed.
     */
    suspend fun interceptDragEvent(event: MotionEvent): Boolean {
        if (!isLayoutLocked()) {
            return true // Allow event
        }

        // Layout is locked, request authentication
        return performAuthentication()
    }

    /**
     * Intercepts long-click (edit mode) and enforces authentication if locked.
     */
    suspend fun interceptLongClickEvent(): Boolean {
        if (!isLayoutLocked()) {
            return true // Allow event
        }

        return performAuthentication()
    }

    /**
     * Core authentication logic: Attempts biometric first, falls back to PIN.
     */
    private suspend fun performAuthentication(): Boolean {
        val lockState = layoutLockDao.getLayoutLockState().let { flow ->
            var result: LayoutLockState? = null
            flow.collect { result = it }
            result
        } ?: return false

        return when (lockState.lockMethod) {
            "biometric" -> {
                // Biometric authentication attempt
                var authSuccess = false
                biometricManager.authenticate(context as androidx.fragment.app.FragmentActivity).collect { result ->
                    authSuccess = result is BiometricAuthResult.Success
                }
                if (authSuccess) {
                    _authenticationResult.postValue(AuthenticationResult.Success)
                    true
                } else {
                    _authenticationResult.postValue(AuthenticationResult.Failed("Biometric auth failed"))
                    false
                }
            }
            "pin" -> {
                // PIN authentication is handled via UI dialog
                // This method would be called after PIN entry
                true // Placeholder; actual PIN verification happens in activity
            }
            else -> false
        }
    }

    /**
     * Verifies a PIN for layout unlock.
     */
    suspend fun verifyPinForUnlock(pin: String): Boolean {
        return pinSecurityManager.verifyPin(pin).also { success ->
            if (success) {
                _authenticationResult.postValue(AuthenticationResult.Success)
            } else {
                _authenticationResult.postValue(AuthenticationResult.Failed("Invalid PIN"))
            }
        }
    }
}

/**
 * Result of authentication attempt.
 */
sealed class AuthenticationResult {
    object Success : AuthenticationResult()
    data class Failed(val message: String) : AuthenticationResult()
}
