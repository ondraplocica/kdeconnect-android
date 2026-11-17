/*
 * SPDX-FileCopyrightText: 2024 Albert Vaca Cintora <albertvaka@gmail.com>
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui.compose

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// KDE-blue palette, used when the device has no dynamic (wallpaper) colours (Android < 12).
private val KdeLightColors = lightColorScheme(
    primary = Color(0xFF00629E), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCFE5FF), onPrimaryContainer = Color(0xFF001D33),
    secondary = Color(0xFF526070), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD5E4F7), onSecondaryContainer = Color(0xFF0F1D2A),
    tertiary = Color(0xFF68587A), onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFF0DBFF), onTertiaryContainer = Color(0xFF231533),
    background = Color(0xFFF8F9FF), onBackground = Color(0xFF191C20),
    surface = Color(0xFFF8F9FF), onSurface = Color(0xFF191C20),
    surfaceVariant = Color(0xFFDFE3EB), onSurfaceVariant = Color(0xFF42474E),
    outline = Color(0xFF72777F), outlineVariant = Color(0xFFC2C7CF),
    surfaceContainerLowest = Color(0xFFFFFFFF), surfaceContainerLow = Color(0xFFF2F3F9),
    surfaceContainer = Color(0xFFECEEF4), surfaceContainerHigh = Color(0xFFE6E8EE), surfaceContainerHighest = Color(0xFFE0E2E8),
)

private val KdeDarkColors = darkColorScheme(
    primary = Color(0xFF9ACBFF), onPrimary = Color(0xFF003355),
    primaryContainer = Color(0xFF00497A), onPrimaryContainer = Color(0xFFCFE5FF),
    secondary = Color(0xFFB9C8DA), onSecondary = Color(0xFF243240),
    secondaryContainer = Color(0xFF3A4857), onSecondaryContainer = Color(0xFFD5E4F7),
    tertiary = Color(0xFFD7BEE8), onTertiary = Color(0xFF3B2B4D),
    tertiaryContainer = Color(0xFF524164), onTertiaryContainer = Color(0xFFF0DBFF),
    background = Color(0xFF101418), onBackground = Color(0xFFE0E2E8),
    surface = Color(0xFF101418), onSurface = Color(0xFFE0E2E8),
    surfaceVariant = Color(0xFF42474E), onSurfaceVariant = Color(0xFFC2C7CF),
    outline = Color(0xFF8C9199), outlineVariant = Color(0xFF42474E),
    surfaceContainerLowest = Color(0xFF0B0F13), surfaceContainerLow = Color(0xFF191C20),
    surfaceContainer = Color(0xFF1D2024), surfaceContainerHigh = Color(0xFF272A2F), surfaceContainerHighest = Color(0xFF32353A),
)

// Rounder, friendlier shapes than the Material defaults.
private val KdeShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

// Same scale as Material, with heavier weights on titles so cards read with clear hierarchy.
private val Base = Typography()
private val KdeTypography = Typography(
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
)

@Composable
fun KdeTheme(context : Context, content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (dark) KdeDarkColors else KdeLightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = KdeShapes,
        typography = KdeTypography,
        content = content,
    )
}
