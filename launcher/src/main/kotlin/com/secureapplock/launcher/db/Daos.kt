package com.secureapplock.launcher.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LayoutLockDao {

    @Query("SELECT * FROM layout_lock_state WHERE id = 1")
    fun getLayoutLockState(): Flow<LayoutLockState?>

    @Query("SELECT isLocked FROM layout_lock_state WHERE id = 1")
    suspend fun isLayoutLocked(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLockState(state: LayoutLockState)

    @Update
    suspend fun updateLockState(state: LayoutLockState)

    @Query("UPDATE layout_lock_state SET isLocked = :isLocked WHERE id = 1")
    suspend fun setLayoutLocked(isLocked: Boolean)

    @Query("UPDATE layout_lock_state SET lockMethod = :method WHERE id = 1")
    suspend fun setLockMethod(method: String)
}

@Dao
interface WidgetAssignmentDao {

    @Query("SELECT * FROM widget_assignments")
    fun getAllAssignments(): Flow<List<WidgetAssignment>>

    @Query("SELECT * FROM widget_assignments WHERE appPackageName = :packageName")
    suspend fun getAssignmentForApp(packageName: String): WidgetAssignment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: WidgetAssignment)

    @Update
    suspend fun updateAssignment(assignment: WidgetAssignment)

    @Delete
    suspend fun deleteAssignment(assignment: WidgetAssignment)

    @Query("DELETE FROM widget_assignments WHERE appPackageName = :packageName")
    suspend fun deleteAssignmentForApp(packageName: String)
}

@Dao
interface AppGridPositionDao {

    @Query("SELECT * FROM app_grid_positions")
    fun getAllPositions(): Flow<List<AppGridPosition>>

    @Query("SELECT * FROM app_grid_positions WHERE appPackageName = :packageName")
    suspend fun getPositionForApp(packageName: String): AppGridPosition?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosition(position: AppGridPosition)

    @Update
    suspend fun updatePosition(position: AppGridPosition)

    @Delete
    suspend fun deletePosition(position: AppGridPosition)

    @Query("DELETE FROM app_grid_positions WHERE appPackageName = :packageName")
    suspend fun deletePositionForApp(packageName: String)

    @Query("SELECT COUNT(*) FROM app_grid_positions")
    suspend fun getTotalAppsPositioned(): Int
}
