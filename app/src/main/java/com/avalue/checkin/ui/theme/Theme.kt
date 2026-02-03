package com.avalue.checkin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand Colors
val Primary = Color(0xFF1565C0)
val PrimaryDark = Color(0xFF0D47A1)
val PrimaryLight = Color(0xFF42A5F5)
val Secondary = Color(0xFF00897B)
val SecondaryDark = Color(0xFF00695C)
val Success = Color(0xFF2E7D32)
val SuccessLight = Color(0xFF4CAF50)
val Error = Color(0xFFC62828)
val ErrorLight = Color(0xFFEF5350)
val Background = Color(0xFFF5F5F5)
val Surface = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF212121)
val TextSecondary = Color(0xFF757575)

// Overlay Colors
val OverlayDark = Color(0x99000000)
val OverlayLight = Color(0x4DFFFFFF)
val GuideBorder = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryDark,
    error = Error,
    onError = Color.White,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = TextSecondary
)

@Composable
fun PatientCheckInTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
