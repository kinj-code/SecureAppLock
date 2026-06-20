package com.secureapplock.launcher.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FontPreferenceDao {

    @Query("SELECT * FROM font_preferences WHERE id = 1")
    fun getFontPreference(): Flow<FontPreference?>

    @Query("SELECT * FROM font_preferences WHERE id = 1")
    suspend fun getFontPreferenceSuspend(): FontPreference?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFontPreference(preference: FontPreference)

    @Update
    suspend fun updateFontPreference(preference: FontPreference)

    @Query("UPDATE font_preferences SET displayFontFamily = :fontFamily WHERE id = 1")
    suspend fun setDisplayFont(fontFamily: String)

    @Query("UPDATE font_preferences SET headingFontFamily = :fontFamily WHERE id = 1")
    suspend fun setHeadingFont(fontFamily: String)

    @Query("UPDATE font_preferences SET bodyFontFamily = :fontFamily WHERE id = 1")
    suspend fun setBodyFont(fontFamily: String)

    @Query("UPDATE font_preferences SET bodyFontSize = :fontSize WHERE id = 1")
    suspend fun setBodyFontSize(fontSize: Float)

    @Query("UPDATE font_preferences SET headingFontSize = :fontSize WHERE id = 1")
    suspend fun setHeadingFontSize(fontSize: Float)

    @Query("UPDATE font_preferences SET displayFontSize = :fontSize WHERE id = 1")
    suspend fun setDisplayFontSize(fontSize: Float)

    @Query("UPDATE font_preferences SET lineSpacing = :spacing WHERE id = 1")
    suspend fun setLineSpacing(spacing: Float)

    @Query("UPDATE font_preferences SET letterSpacing = :spacing WHERE id = 1")
    suspend fun setLetterSpacing(spacing: Float)
}
