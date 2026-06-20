package com.secureapplock.launcher.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.secureapplock.launcher.databinding.ActivityTypographyShowcaseBinding
import com.secureapplock.launcher.db.LauncherDatabase
import com.secureapplock.launcher.typography.FontManager
import com.secureapplock.launcher.typography.setDisplayFont
import com.secureapplock.launcher.typography.setHeadingFont
import com.secureapplock.launcher.typography.setBodyFont
import com.secureapplock.launcher.typography.setMonospaceFont
import com.secureapplock.launcher.typography.setDecorativeFont
import com.secureapplock.launcher.typography.setLightFont
import kotlinx.coroutines.launch

/**
 * TypographyShowcaseActivity: Demonstrates all typography styles available in the launcher.
 */
class TypographyShowcaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTypographyShowcaseBinding
    private lateinit var database: LauncherDatabase
    private lateinit var fontManager: FontManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTypographyShowcaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = LauncherDatabase.getInstance(this)
        fontManager = FontManager(this, database)

        setupTypography()
        observeFontPreferences()
    }

    private fun setupTypography() {
        // Apply fonts using extension functions
        binding.displayTitle.setDisplayFont()
        binding.headingSubtitle.setHeadingFont()
        binding.bodyDescription.setBodyFont()
        binding.sectionHeading.setHeadingFont()
        binding.bodyItem1.setBodyFont()
        binding.bodyItem2.setBodyFont()
        binding.captionText.setLightFont()
        binding.monospaceTe ext.setMonospaceFont()
        binding.decorativeHeader.setDecorativeFont()
    }

    private fun observeFontPreferences() {
        lifecycleScope.launch {
            fontManager.getFontPreference().collect { preference ->
                if (preference != null) {
                    // Apply font size preferences
                    binding.displayTitle.textSize = preference.displayFontSize
                    binding.headingSubtitle.textSize = preference.headingFontSize
                    binding.bodyDescription.textSize = preference.bodyFontSize
                    binding.bodyItem1.textSize = preference.bodyFontSize
                    binding.bodyItem2.textSize = preference.bodyFontSize

                    // Apply line spacing
                    binding.bodyDescription.setLineSpacing(0f, preference.lineSpacing)
                    binding.bodyItem1.setLineSpacing(0f, preference.lineSpacing)
                    binding.bodyItem2.setLineSpacing(0f, preference.lineSpacing)
                }
            }
        }
    }
}
