package cat.nilcm01.portam.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// General colors

val ColorWhiteAprox = Color(0xFFEEEEEE)
val ColorWhiteTotal = Color(0xFFFFFFFF)

val ColorBlackAprox = Color(0xFF111111)
val ColorBlackTotal = Color(0xFF000000)


// Main colors

val ColorPrimary = Color(0xFF00A1D8)
val ColorOnPrimary = ColorWhiteAprox

val ColorSecondary = Color(0xFF94C3D8)
val ColorOnSecondary = ColorBlackAprox

val ColorTertiary = Color(0xFF9F9F9F)
val ColorOnTertiary = ColorWhiteAprox

// Theme colors

val ColorLightBackground = ColorWhiteAprox
val ColorDarkBackground = ColorBlackTotal

// Other colors

val ColorRed = Color(0xFFD32F2F)
val ColorGreen = Color(0xFF388E3C)
val ColorYellow = Color(0xFFFBC02D)

// Transparent color
val ColorTransparent = Color(0x00000000)

// Definition of custom colors into the scheme
private val ColorScheme.isLight: Boolean
    get() = background.luminance() > 0.5

val ColorScheme.transparent: Color
    get() = ColorTransparent

val ColorScheme.success: Color
    get() = if (isLight) ColorGreen else Color(0xFF66BB6A)

val ColorScheme.error: Color
    get() = if (isLight) ColorRed else Color(0xFFEF5350)

val ColorScheme.warning: Color
    get() = if (isLight) ColorYellow else Color(0xFFFFEE58)
