package com.secureapplock.launcher.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Extended Launcher Database: Stores layout, widget assignments, lock states, and new features.
 */
@Database(
    entities = [
        LayoutLockState::class,
        WidgetAssignment::class,
        AppGridPosition::class,
        ScreenPage::class,
        CustomGesture::class,
        WidgetType::class,
        ThemeConfig::class,
        TransitionAnimation::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LauncherDatabase : RoomDatabase() {

    // Existing DAOs
    abstract fun layoutLockDao(): LayoutLockDao
    abstract fun widgetAssignmentDao(): WidgetAssignmentDao
    abstract fun appGridPositionDao(): AppGridPositionDao

    // New DAOs
    abstract fun screenPageDao(): ScreenPageDao
    abstract fun customGestureDao(): CustomGestureDao
    abstract fun widgetTypeDao(): WidgetTypeDao
    abstract fun themeConfigDao(): ThemeConfigDao
    abstract fun transitionAnimationDao(): TransitionAnimationDao

    companion object {
        @Volatile
        private var INSTANCE: LauncherDatabase? = null

        fun getInstance(context: Context): LauncherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LauncherDatabase::class.java,
                    "launcher_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
