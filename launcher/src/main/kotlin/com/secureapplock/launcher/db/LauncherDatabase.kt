package com.secureapplock.launcher.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Launcher Database: Stores layout, widget assignments, and lock states.
 */
@Database(
    entities = [
        LayoutLockState::class,
        WidgetAssignment::class,
        AppGridPosition::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class LauncherDatabase : RoomDatabase() {

    abstract fun layoutLockDao(): LayoutLockDao
    abstract fun widgetAssignmentDao(): WidgetAssignmentDao
    abstract fun appGridPositionDao(): AppGridPositionDao

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
