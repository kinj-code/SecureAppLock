package com.secureapplock.launcher.paging

import android.content.Context
import androidx.viewpager2.widget.ViewPager2
import com.secureapplock.launcher.db.LauncherDatabase
import kotlinx.coroutines.flow.Flow

/**
 * MultiScreenPagingManager: Handles horizontal paging for multiple home screen pages.
 * Supports screen transitions, page management, and wallpaper per-screen.
 */
class MultiScreenPagingManager(
    private val context: Context,
    private val database: LauncherDatabase,
    private val viewPager: ViewPager2
) {

    private val screenPageDao = database.screenPageDao()

    /**
     * Initializes the paging manager with default screens.
     */
    suspend fun initialize() {
        val totalPages = screenPageDao.getTotalPages()
        if (totalPages == 0) {
            // Create default pages
            for (i in 0..2) {
                screenPageDao.insertPage(
                    com.secureapplock.launcher.db.ScreenPage(
                        screenIndex = i,
                        name = "Screen ${i + 1}"
                    )
                )
            }
        }
    }

    /**
     * Gets the list of all pages.
     */
    fun getAllPages(): Flow<List<com.secureapplock.launcher.db.ScreenPage>> {
        return screenPageDao.getAllPages()
    }

    /**
     * Gets the current page index.
     */
    fun getCurrentPageIndex(): Int {
        return viewPager.currentItem
    }

    /**
     * Navigates to a specific page.
     */
    fun navigateToPage(pageIndex: Int, smooth: Boolean = true) {
        viewPager.setCurrentItem(pageIndex, smooth)
    }

    /**
     * Adds a new screen page.
     */
    suspend fun addScreenPage(): Boolean {
        val totalPages = screenPageDao.getTotalPages()
        if (totalPages >= 5) {
            // Limit to 5 screens
            return false
        }

        screenPageDao.insertPage(
            com.secureapplock.launcher.db.ScreenPage(
                screenIndex = totalPages,
                name = "Screen ${totalPages + 1}"
            )
        )
        return true
    }

    /**
     * Removes a screen page.
     */
    suspend fun removeScreenPage(screenIndex: Int): Boolean {
        val totalPages = screenPageDao.getTotalPages()
        if (totalPages <= 1) {
            // Must keep at least 1 screen
            return false
        }

        screenPageDao.deletePageByIndex(screenIndex)
        return true
    }

    /**
     * Sets up page change listener.
     */
    fun setupPageChangeListener(listener: (pageIndex: Int) -> Unit) {
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                listener(position)
            }
        })
    }

    /**
     * Gets the wallpaper for a specific page.
     */
    suspend fun getPageWallpaper(screenIndex: Int): Int? {
        return screenPageDao.getPage(screenIndex)?.wallpaperId
    }

    /**
     * Sets the wallpaper for a specific page.
     */
    suspend fun setPageWallpaper(screenIndex: Int, wallpaperId: Int) {
        val page = screenPageDao.getPage(screenIndex) ?: return
        screenPageDao.updatePage(page.copy(wallpaperId = wallpaperId))
    }
}
