package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LifeLinkColorScheme = darkColorScheme(
    primary = CrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = DeepBloodRed,
    onPrimaryContainer = TextSoftRose,
    secondary = RubyAccent,
    onSecondary = ObsidianBlack,
    secondaryContainer = GlassCardElevated,
    onSecondaryContainer = TextSoftRose,
    tertiary = MedicalEmerald,
    onTertiary = ObsidianBlack,
    tertiaryContainer = MedicalEmeraldBg,
    onTertiaryContainer = MedicalEmerald,
    background = ObsidianBlack,
    onBackground = TextAlabaster,
    surface = CrimsonVoid,
    onSurface = TextAlabaster,
    surfaceVariant = GlassCardSurface,
    onSurfaceVariant = TextMutedMauve,
    outline = DarkCrimsonBorder,
    outlineVariant = BrightCrimsonBorder,
    error = EmergencyPulseRed,
    onError = Color.White,
    errorContainer = EmergencyDarkBg,
    onErrorContainer = RubyAccent
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LifeLinkColorScheme,
        typography = Typography,
        content = content
    )
}
