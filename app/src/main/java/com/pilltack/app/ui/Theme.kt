package com.pilltack.app.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/**
 * Colours from the PillTrack Behance palette, with a dark counterpart for each.
 * Every colour reads [dark], so switching the theme recolours the whole app instantly.
 */
object PT {
    var dark by mutableStateOf(false)

    val Brand = Color(0xFF164F7A)                       // fixed brand blue (splash, onboarding, alarm screen)
    val Blue: Color get() = if (dark) Color(0xFF2F7EC1) else Brand
    val Bg: Color get() = if (dark) Color(0xFF101114) else Color(0xFFFFFFFF)
    val Surface: Color get() = if (dark) Color(0xFF2B2F35) else Color(0xFFF3F3F3)
    val Dialog: Color get() = if (dark) Color(0xFF24272C) else Color(0xFFFFFFFF)
    val Ink: Color get() = if (dark) Color(0xFFECEFF3) else Color(0xFF242424)
    val Grey: Color get() = if (dark) Color(0xFF9AA3AD) else Color(0xFF999999)
    val Link: Color get() = if (dark) Color(0xFF7DB6FF) else Color(0xFF1E6FD9)
    val Green: Color get() = if (dark) Color(0xFF4CC36B) else Color(0xFF2EA44F)
    val Red: Color get() = if (dark) Color(0xFFFF6B6F) else Color(0xFFE5383B)
    val Pill: Color get() = if (dark) Color(0xFF2A2C31) else Color(0xFFF1F3F6)   // floating bottom bar (light in light mode)
}

/** Design type scale (Satoshi). Add Satoshi .ttf files under res/font and replace SansSerif below. */
object T {
    private val f = FontFamily.SansSerif
    val h1 = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 30.sp)
    val h2 = TextStyle(fontFamily = f, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    val b1 = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 17.sp)
    val b2 = TextStyle(fontFamily = f, fontWeight = FontWeight.Medium, fontSize = 16.sp)
    val b3 = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 15.sp)
    val c1 = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 14.sp)
    val c2 = TextStyle(fontFamily = f, fontWeight = FontWeight.Normal, fontSize = 12.sp)
}

/** mode: "SYSTEM", "LIGHT" or "DARK". */
@Composable
fun PillTrackTheme(mode: String = "SYSTEM", content: @Composable () -> Unit) {
    val dark = when (mode) { "DARK" -> true; "LIGHT" -> false; else -> isSystemInDarkTheme() }
    if (PT.dark != dark) PT.dark = dark
    val scheme = if (dark) darkColorScheme(
        primary = PT.Blue, onPrimary = Color.White, background = PT.Bg, surface = PT.Bg, onSurface = PT.Ink,
        surfaceVariant = PT.Surface, outline = PT.Grey, error = PT.Red
    ) else lightColorScheme(
        primary = PT.Blue, onPrimary = Color.White, background = PT.Bg, surface = PT.Bg, onSurface = PT.Ink,
        surfaceVariant = PT.Surface, outline = PT.Grey, error = PT.Red
    )
    MaterialTheme(colorScheme = scheme,
        typography = Typography(bodyLarge = T.b3, bodyMedium = T.c1, titleLarge = T.h2, labelLarge = T.b2),
        content = content)
}

/** Colours the status and navigation bars and picks light/dark icons. */
@Composable
fun SystemBars(status: Color, nav: Color, lightBg: Boolean) {
    val view = LocalView.current
    SideEffect {
        val w = (view.context as? Activity)?.window ?: return@SideEffect
        w.statusBarColor = status.toArgb()
        w.navigationBarColor = nav.toArgb()
        val c = WindowCompat.getInsetsController(w, view)
        c.isAppearanceLightStatusBars = lightBg
        c.isAppearanceLightNavigationBars = lightBg
    }
}
