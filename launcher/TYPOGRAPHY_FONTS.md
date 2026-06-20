# Typography & Font System - SecureAppLock Launcher

## Overview

The launcher now includes a comprehensive typography and font system with support for multiple font families, sizes, and styles. This ensures consistent, professional text rendering throughout the app.

## Font Families Included

### 1. **Poppins**
- **Usage:** Display titles, large headlines
- **Weights:** Regular (400), SemiBold (600), Bold (700)
- **Best for:** Hero text, app title, section headers

### 2. **Inter**
- **Usage:** Body text, headings, general UI
- **Weights:** Light (300), Regular (400), SemiBold (600), Bold (700)
- **Best for:** Main body copy, interface text, labels
- **Note:** Excellent readability, modern, widely used

### 3. **Roboto Mono**
- **Usage:** Code, technical text, numbers
- **Weights:** Regular (400), Bold (700)
- **Best for:** Package names, hex codes, technical display

### 4. **Playfair Display**
- **Usage:** Decorative headers, special emphasis
- **Weights:** Bold (700)
- **Best for:** Premium sections, special UI elements

### 5. **Open Sans** (Optional)
- **Usage:** Alternative body font, versatile
- **Weights:** Regular (400), SemiBold (600), Bold (700)
- **Best for:** Alternative styling, compatibility

## Typography Styles

### Display Styles
```xml
style="@style/Typography.Display.Large"       <!-- 40sp, Poppins Bold -->
style="@style/Typography.Display.Medium"      <!-- 32sp, Poppins Bold -->
style="@style/Typography.Display.Small"       <!-- 24sp, Poppins Bold -->
```

### Heading Styles
```xml
style="@style/Typography.Heading.Large"       <!-- 28sp, Inter Bold -->
style="@style/Typography.Heading.Medium"      <!-- 24sp, Inter Bold -->
style="@style/Typography.Heading.Small"       <!-- 20sp, Inter Bold -->
```

### Body Styles
```xml
style="@style/Typography.Body.Large"          <!-- 18sp, Inter Regular -->
style="@style/Typography.Body.Medium"         <!-- 16sp, Inter Regular -->
style="@style/Typography.Body.Small"          <!-- 14sp, Inter Regular -->
```

### Special Styles
```xml
style="@style/Typography.Subheading"          <!-- 18sp, Inter Bold -->
style="@style/Typography.Caption"             <!-- 12sp, Inter, light color -->
style="@style/Typography.Overline"            <!-- 11sp, Inter Bold, uppercase -->
style="@style/Typography.Label"               <!-- 14sp, Inter Bold -->
style="@style/Typography.Monospace"           <!-- 12sp, Roboto Mono -->
style="@style/Typography.Decorative"          <!-- 28sp, Playfair Display Bold -->
style="@style/Typography.Light"               <!-- 16sp, Inter Light -->
```

## Key Components

### 1. **TypefaceManager**
```kotlin
val typefaceManager = TypefaceManager(context)

// Get typefaces
val displayTypeface = typefaceManager.getDisplayTypeface()      // Poppins Bold
val headingTypeface = typefaceManager.getHeadingTypeface()      // Inter SemiBold
val bodyTypeface = typefaceManager.getBodyTypeface()            // Inter Regular
val monoTypeface = typefaceManager.getMonospaceTypeface()       // Roboto Mono
val decorativeTypeface = typefaceManager.getDecorativeTypeface() // Playfair Bold

// Get by family name
val typeface = typefaceManager.getTypefaceByFamily("body")
```

### 2. **Typography Extension Functions**
```kotlin
// Easy font application to TextViews
textView.setDisplayFont()           // Apply Poppins Bold
textView.setHeadingFont()           // Apply Inter SemiBold
textView.setBodyFont()              // Apply Inter Regular
textView.setMonospaceFont()         // Apply Roboto Mono
textView.setDecorativeFont()        // Apply Playfair Bold
textView.setLightFont()             // Apply Inter Light
```

### 3. **FontManager**
```kotlin
val fontManager = FontManager(context, database)

// Initialize defaults
fontManager.initializeDefaultFonts()

// Get preferences
fontManager.getFontPreference()                 // Flow<FontPreference>

// Customize fonts
fontManager.setDisplayFont(FontManager.FONT_POPPINS)
fontManager.setHeadingFont(FontManager.FONT_INTER)
fontManager.setBodyFont(FontManager.FONT_INTER)

// Customize sizes
fontManager.setBodyFontSize(16f)                // 10f - 30f
fontManager.setHeadingFontSize(24f)             // 14f - 32f
fontManager.setDisplayFontSize(32f)             // 18f - 40f

// Customize spacing
fontManager.setLineSpacing(1.5f)                // 1f - 2.5f
fontManager.setLetterSpacing(0.015f)            // 0f - 5f

// Reset to defaults
fontManager.resetToDefaults()
```

## Font Preferences Database

**Entity:** `FontPreference`

```kotlin
@Entity(tableName = "font_preferences")
data class FontPreference(
    val displayFontFamily: String,      // "poppins", "inter", etc.
    val headingFontFamily: String,
    val bodyFontFamily: String,
    val bodyFontSize: Float,            // 10f - 30f
    val headingFontSize: Float,         // 14f - 32f
    val displayFontSize: Float,         // 18f - 40f
    val lineSpacing: Float,             // 1f - 2.5f
    val letterSpacing: Float,           // 0f - 5f
    val enableTextShadow: Boolean,
    val textShadowColor: String,
    val textShadowRadius: Float
)
```

## Available Font Families

```kotlin
const val FONT_POPPINS = "poppins"       // Display titles
const val FONT_INTER = "inter"           // Body & headings
const val FONT_OPENSANS = "opensans"     // Alternative body
const val FONT_ROBOTO = "roboto"         // General purpose
const val FONT_PLAYFAIR = "playfair"     // Decorative
const val FONT_LATO = "lato"             // Alternative
const val FONT_MONTSERRAT = "montserrat" // Modern
const val FONT_RALEWAY = "raleway"       // Elegant
```

## XML Usage Examples

### Display Title
```xml
<TextView
    android:id="@+id/app_title"
    style="@style/Typography.Display.Large"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="SecureAppLock" />
```

### Section Heading
```xml
<TextView
    android:id="@+id/section_heading"
    style="@style/Typography.Heading.Medium"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Features" />
```

### Body Text
```xml
<TextView
    android:id="@+id/description"
    style="@style/Typography.Body.Medium"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Description text here" />
```

### Monospace Code
```xml
<TextView
    android:id="@+id/package_name"
    style="@style/Typography.Monospace"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="com.secureapplock.launcher" />
```

### Caption/Helper Text
```xml
<TextView
    android:id="@+id/helper_text"
    style="@style/Typography.Caption"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="This is helper text" />
```

## Kotlin Usage Examples

### Using Extension Functions
```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val titleView = findViewById<TextView>(R.id.title)
        val bodyView = findViewById<TextView>(R.id.body)
        val codeView = findViewById<TextView>(R.id.code)
        
        // Apply fonts easily
        titleView.setDisplayFont()      // Poppins Bold
        bodyView.setBodyFont()          // Inter Regular
        codeView.setMonospaceFont()     // Roboto Mono
    }
}
```

### Using TypefaceManager
```kotlin
val typefaceManager = TypefaceManager(context)

titleView.typeface = typefaceManager.getDisplayTypeface()
bodyView.typeface = typefaceManager.getBodyTypeface()
```

### Using FontManager for Customization
```kotlin
val fontManager = FontManager(context, database)

lifecycleScope.launch {
    fontManager.initializeDefaultFonts()
    
    // Get current preferences
    fontManager.getFontPreference().collect { preference ->
        if (preference != null) {
            // Update UI with custom sizes
            titleView.textSize = preference.displayFontSize
            bodyView.textSize = preference.bodyFontSize
            bodyView.setLineSpacing(0f, preference.lineSpacing)
        }
    }
    
    // Allow user to customize
    fontManager.setBodyFontSize(18f)    // Make body text larger
    fontManager.setLineSpacing(1.7f)    // Increase line spacing
}
```

## Typography Hierarchy

```
Display (40sp)      - Hero text, app title
  ↓
Heading Large (28sp)    - Major section headers
  ↓
Heading Medium (24sp)   - Section headers
  ↓
Subheading (18sp)       - Subsection headers
  ↓
Heading Small (20sp)    - Minor headers
  ↓
Body Large (18sp)       - Prominent body text
  ↓
Body Medium (16sp)      - Default body text
  ↓
Body Small (14sp)       - Secondary body text
  ↓
Label (14sp)            - Button labels, tags
  ↓
Caption (12sp)          - Helper text, hints
  ↓
Monospace (12sp)        - Code, technical
  ↓
Overline (11sp)         - Tags, categories
```

## Best Practices

1. **Hierarchy:** Use Display > Heading > Body > Caption for clear visual hierarchy
2. **Consistency:** Use the same font for text of the same type (all body text = Inter Regular)
3. **Readability:** Maintain 1.5x line spacing for body text minimum
4. **Accessibility:** Use adequate font size (14sp minimum for body text)
5. **Emphasis:** Use Poppins Bold for important titles, Inter SemiBold for medium emphasis
6. **Decorative:** Reserve Playfair Display for special/premium sections only
7. **Code:** Always use Roboto Mono for technical text

## Font Files to Include

Add the following font files to `res/font/`:
- `poppins_regular.ttf`
- `poppins_semibold.ttf`
- `poppins_bold.ttf`
- `inter_light.ttf`
- `inter_regular.ttf`
- `inter_semibold.ttf`
- `inter_bold.ttf`
- `roboto_mono_regular.ttf`
- `roboto_mono_bold.ttf`
- `playfair_display_bold.ttf`
- `opensans_regular.ttf`
- `opensans_semibold.ttf`
- `opensans_bold.ttf`

## Performance Considerations

- Fonts are loaded on-demand via ResourcesCompat
- Font caching is handled by the framework
- Typeface objects are reused where possible
- Database queries for font preferences use Flow (non-blocking)

## Future Enhancements

1. Font selection UI in settings
2. Font size adjustment slider
3. Line spacing customization UI
4. Letter spacing fine-tuning
5. Text shadow effects
6. Font preview panel
7. Export/import font profiles
8. Per-activity font customization
