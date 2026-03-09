package noor.serry.designsystem.design

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColorScheme(
    // Brand
    val primary: Color,
    val primaryLight: Color,
    val secondary: Color,
    val secondaryHover: Color,

    // Text
    val text: Color,
    val textSecondary: Color,


    // Background
    val bg: Color,
    val bgSecondary: Color,
    val bgHover: Color,
    val bgDisabled: Color,

    // Border
    val border: Color,
    val borderHover: Color,
    val borderFocus: Color,

    // Success
    val success: Color,
    val successBg: Color,
    val successText: Color,
    val successBorder: Color,

    // Warning
    val warning: Color,
    val warningBg: Color,
    val warningText: Color,
    val warningBorder: Color,

    // Error
    val error: Color,
    val errorBg: Color,
    val errorText: Color,
    val errorBorder: Color,
)

val darkThemeColors = AppColorScheme(
    // Brand
    primary = Color(0xFF1F2C47),          // primary-500
    primaryLight = Color(0xFF11345F),     // primary-900

    secondary = Color(0xFFE2BF32),        // secondary-500
    secondaryHover = Color(0xFFC9AA2C),   // secondary-400

    // Text
    text = Color(0xFF000000),             // primary-100
    textSecondary = Color(0xFF64748B),    // gray-100
       // primary-100

    // Background
    bg = Color(0xFFFFFFFF),               // black (mapped to #ffffff in dark primitives)
    bgSecondary = Color(0xFFF3F3F3),      // gray-900
    bgHover = Color(0xFFECECEC),          // gray-800
    bgDisabled = Color(0xFFECECEC),       // gray-800

    // Border
    border = Color(0xFFE2E8F0),           // gray-700
    borderHover = Color(0xFF828282),      // gray-600
    borderFocus = Color(0xFF192339),      // primary-400

    // Success
    success = Color(0xFF00790E),          // success-500
    successBg = Color(0xFFE6F3E7),        // success-950
    successText = Color(0xFF00650C),      // success-300
    successBorder = Color(0xFFB0D9B5),    // success-700

    // Warning
    warning = Color(0xFFE69D00),          // warning-500
    warningBg = Color(0xFFFFF7E6),        // warning-950
    warningText = Color(0xFFBF8300),      // warning-300
    warningBorder = Color(0xFFFFE6B0),    // warning-700

    // Error
    error = Color(0xFFA70000),            // error-500
    errorBg = Color(0xFFF8E6E6),          // error-950
    errorText = Color(0xFF8C0000),        // error-300
    errorBorder = Color(0xFFEAB0B0),      // error-700
)

internal val LocalAppColors = staticCompositionLocalOf { darkThemeColors }