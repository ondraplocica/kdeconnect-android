/*
 * SPDX-FileCopyrightText: 2026 Ondrej Plocica
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui.compose.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Rounded tonal surface that springs down slightly while pressed. */
@Composable
fun ModernCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    container: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    shape: Shape = MaterialTheme.shapes.large,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "press",
    )
    val animated = modifier.graphicsLayer { scaleX = scale; scaleY = scale }
    if (onClick != null) {
        Surface(onClick = onClick, modifier = animated, shape = shape, color = container, interactionSource = interaction, content = content)
    } else {
        Surface(modifier = animated, shape = shape, color = container, content = content)
    }
}

/** A drawable inside a tonal circle (device type, plugin icon, banner icon...). */
@Composable
fun TonalAvatar(
    iconRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp,
    container: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier = modifier.size(size).clip(CircleShape).background(container),
        contentAlignment = Alignment.Center,
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

/** Small rounded label, optionally with a leading status dot. */
@Composable
fun StatusPill(
    text: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
    dot: Boolean = false,
) {
    Surface(modifier = modifier, shape = CircleShape, color = container) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (dot) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(content))
                Spacer(Modifier.width(6.dp))
            }
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = content, maxLines = 1)
        }
    }
}
