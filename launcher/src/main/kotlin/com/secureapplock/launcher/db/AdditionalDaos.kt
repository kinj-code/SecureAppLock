package com.secureapplock.launcher.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenPageDao {

    @Query("SELECT * FROM screen_pages ORDER BY screenIndex ASC")
    fun getAllPages(): Flow<List<ScreenPage>>

    @Query("SELECT * FROM screen_pages WHERE screenIndex = :screenIndex")
    suspend fun getPage(screenIndex: Int): ScreenPage?

    @Query("SELECT COUNT(*) FROM screen_pages")
    suspend fun getTotalPages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: ScreenPage)

    @Update
    suspend fun updatePage(page: ScreenPage)

    @Delete
    suspend fun deletePage(page: ScreenPage)

    @Query("DELETE FROM screen_pages WHERE screenIndex = :screenIndex")
    suspend fun deletePageByIndex(screenIndex: Int)
}

@Dao
interface CustomGestureDao {

    @Query("SELECT * FROM custom_gestures WHERE isEnabled = 1")
    fun getEnabledGestures(): Flow<List<CustomGesture>>

    @Query("SELECT * FROM custom_gestures WHERE gestureId = :gestureId")
    suspend fun getGestureById(gestureId: String): CustomGesture?

    @Query("SELECT * FROM custom_gestures ORDER BY gestureId")
    fun getAllGestures(): Flow<List<CustomGesture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGesture(gesture: CustomGesture)

    @Update
    suspend fun updateGesture(gesture: CustomGesture)

    @Delete
    suspend fun deleteGesture(gesture: CustomGesture)

    @Query("UPDATE custom_gestures SET isEnabled = :enabled WHERE gestureId = :gestureId")
    suspend fun setGestureEnabled(gestureId: String, enabled: Boolean)
}

@Dao
interface WidgetTypeDao {

    @Query("SELECT * FROM widget_types ORDER BY displayName")
    fun getAllWidgetTypes(): Flow<List<WidgetType>>

    @Query("SELECT * FROM widget_types WHERE widgetTypeId = :widgetTypeId")
    suspend fun getWidgetTypeById(widgetTypeId: String): WidgetType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgetType(widgetType: WidgetType)

    @Update
    suspend fun updateWidgetType(widgetType: WidgetType)

    @Delete
    suspend fun deleteWidgetType(widgetType: WidgetType)
}

@Dao
interface ThemeConfigDao {

    @Query("SELECT * FROM theme_configs ORDER BY displayName")
    fun getAllThemes(): Flow<List<ThemeConfig>>

    @Query("SELECT * FROM theme_configs WHERE themeId = :themeId")
    suspend fun getThemeById(themeId: String): ThemeConfig?

    @Query("SELECT * FROM theme_configs WHERE isCustom = 0 ORDER BY displayName")
    fun getBuiltInThemes(): Flow<List<ThemeConfig>>

    @Query("SELECT * FROM theme_configs WHERE isCustom = 1 ORDER BY displayName")
    fun getCustomThemes(): Flow<List<ThemeConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTheme(theme: ThemeConfig)

    @Update
    suspend fun updateTheme(theme: ThemeConfig)

    @Delete
    suspend fun deleteTheme(theme: ThemeConfig)
}

@Dao
interface TransitionAnimationDao {

    @Query("SELECT * FROM transition_animations WHERE isEnabled = 1 ORDER BY displayName")
    fun getEnabledAnimations(): Flow<List<TransitionAnimation>>

    @Query("SELECT * FROM transition_animations WHERE animationId = :animationId")
    suspend fun getAnimationById(animationId: String): TransitionAnimation?

    @Query("SELECT * FROM transition_animations ORDER BY displayName")
    fun getAllAnimations(): Flow<List<TransitionAnimation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimation(animation: TransitionAnimation)

    @Update
    suspend fun updateAnimation(animation: TransitionAnimation)

    @Delete
    suspend fun deleteAnimation(animation: TransitionAnimation)
}
