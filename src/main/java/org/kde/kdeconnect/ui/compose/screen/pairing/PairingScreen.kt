/*
 * SPDX-FileCopyrightText: 2026 Saul Cintero Chocarro <scintero@gmail.com>
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui.compose.screen.pairing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.kde.kdeconnect.extensions.safeDrawingBottomPadding
import org.kde.kdeconnect.ui.compose.KdeTheme
import org.kde.kdeconnect.ui.compose.components.KdeThemePreviews
import org.kde.kdeconnect.ui.compose.components.ModernCard
import org.kde.kdeconnect.ui.compose.components.StatusPill
import org.kde.kdeconnect.ui.compose.components.TonalAvatar
import org.kde.kdeconnect.ui.compose.model.device.DeviceUiModel
import org.kde.kdeconnect_tp.R

private enum class DeviceStatus { Connected, Available, Remembered }

@Composable
fun PairingScreen(
    uiState: PairingUiState,
    onClick: (String) -> Unit,
    onWifiSettingsClick: () -> Unit = {},
    onNotificationSettingsClick: () -> Unit = {},
    onLocalNetworkPermissionClick: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val pullRefreshState = rememberPullToRefreshState()

    PairingScreenContent(
        lazyListState = lazyListState,
        pullRefreshState = pullRefreshState,
        uiState = uiState,
        onClick = onClick,
        onWifiSettingsClick = onWifiSettingsClick,
        onNotificationSettingsClick = onNotificationSettingsClick,
        onLocalNetworkPermissionClick = onLocalNetworkPermissionClick,
        onRefresh = onRefresh
    )
}

@Composable
private fun PairingScreenContent(
    lazyListState: LazyListState,
    pullRefreshState: PullToRefreshState,
    uiState: PairingUiState,
    onClick: (String) -> Unit,
    onWifiSettingsClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLocalNetworkPermissionClick: () -> Unit,
    onRefresh: () -> Unit = {}
) {
    val anyDevice = uiState.connected.isNotEmpty() || uiState.available.isNotEmpty() || uiState.remembered.isNotEmpty()
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = safeDrawingBottomPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyListState
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // Warnings / hints as banner cards
            item {
                PairingExplanations(
                    uiState = uiState,
                    onWifiSettingsClick = onWifiSettingsClick,
                    onNotificationSettingsClick = onNotificationSettingsClick,
                    onLocalNetworkPermissionClick = onLocalNetworkPermissionClick
                )
            }

            // One-glance summary
            if (anyDevice) {
                item { DevicesOverview(uiState) }
            }

            deviceSection(R.string.category_connected_devices, DeviceStatus.Connected, uiState.connected, showEmpty = true, onClick = onClick)
            deviceSection(R.string.category_not_paired_devices, DeviceStatus.Available, uiState.available, showEmpty = false, onClick = onClick)
            deviceSection(R.string.category_remembered_devices, DeviceStatus.Remembered, uiState.remembered, showEmpty = false, onClick = onClick)

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

private fun LazyListScope.deviceSection(
    titleRes: Int,
    status: DeviceStatus,
    devices: List<DeviceUiModel>,
    showEmpty: Boolean,
    onClick: (String) -> Unit,
) {
    if (devices.isEmpty() && !showEmpty) return
    item(key = "header-$status") { ListSectionHeader(title = stringResource(id = titleRes), count = devices.size) }
    if (devices.isEmpty()) {
        item(key = "empty-$status") { EmptyPlaceholder() }
    } else {
        items(items = devices, key = { it.id }) { device ->
            DeviceCard(device = device, status = status, onClick = onClick)
        }
    }
}

@Composable
private fun ListSectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (count > 0) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = count.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun DevicesOverview(uiState: PairingUiState) {
    val cs = MaterialTheme.colorScheme
    val title = if (uiState.connected.isNotEmpty()) stringResource(R.string.overview_connected, uiState.connected.size)
                else stringResource(R.string.overview_none_connected)
    val subtitle = listOfNotNull(
        uiState.available.size.takeIf { it > 0 }?.let { stringResource(R.string.overview_available, it) },
        uiState.remembered.size.takeIf { it > 0 }?.let { stringResource(R.string.overview_remembered, it) },
    ).joinToString("  ·  ")
    ModernCard(container = cs.primaryContainer, shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TonalAvatar(
                iconRes = R.drawable.ic_phonelink_36dp,
                size = 56.dp,
                container = cs.onPrimaryContainer.copy(alpha = 0.12f),
                tint = cs.onPrimaryContainer,
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.headlineSmall, color = cs.onPrimaryContainer)
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = cs.onPrimaryContainer.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
private fun PairingExplanations(
    uiState: PairingUiState,
    onWifiSettingsClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLocalNetworkPermissionClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (!uiState.hasLocalNetworkPermission) {
            PairingExplanationRow(
                text = stringResource(id = R.string.pairing_missing_local_network_permission),
                icon = R.drawable.ic_warning,
                onClick = onLocalNetworkPermissionClick
            )
        }
        if (!uiState.hasNotificationsPermission) {
            PairingExplanationRow(
                text = stringResource(R.string.no_notifications),
                icon = R.drawable.ic_warning,
                onClick = onNotificationSettingsClick
            )
        }
        if (!uiState.hasLocalNetworkPermission || !uiState.hasNotificationsPermission) {
            return
        }
        if (uiState.hasDuplicateNames) {
            DuplicateNamesWarning()
        }
        val someDevicesReachable = uiState.available.isNotEmpty() || uiState.connected.isNotEmpty()
        if (someDevicesReachable || uiState.isWifiAvailable) {
            if (uiState.isTrustedNetwork) {
                PairingExplanationRow(text = stringResource(R.string.pairing_description))
            } else {
                PairingExplanationRow(
                    text = stringResource(R.string.on_non_trusted_message),
                    icon = R.drawable.ic_warning
                )
            }
        } else {
            PairingExplanationRow(
                text = stringResource(R.string.no_wifi),
                icon = R.drawable.ic_wifi,
                onClick = onWifiSettingsClick
            )
        }
    }
}

/** A hint or warning as a banner card: errors get the error container, hints the secondary one. */
@Composable
fun PairingExplanationRow(
    text: String,
    icon: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val cs = MaterialTheme.colorScheme
    val warning = icon == R.drawable.ic_warning
    val container = if (warning) cs.errorContainer else cs.surfaceContainerHigh
    val content = if (warning) cs.onErrorContainer else cs.onSurfaceVariant
    ModernCard(onClick = onClick, container = container, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                TonalAvatar(iconRes = icon, size = 40.dp, container = content.copy(alpha = 0.12f), tint = content)
                Spacer(Modifier.width(12.dp))
            }
            Text(text = text, style = MaterialTheme.typography.bodyMedium, color = content, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DuplicateNamesWarning() {
    PairingExplanationRow(
        text = stringResource(id = R.string.pairing_duplicate_names),
        icon = R.drawable.ic_warning,
    )
}

@Composable
fun EmptyPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TonalAvatar(
            iconRes = R.drawable.ic_phonelink_off_36dp,
            size = 88.dp,
            container = MaterialTheme.colorScheme.surfaceContainerHigh,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.device_list_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeviceCard(
    device: DeviceUiModel,
    status: DeviceStatus,
    onClick: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val connected = status == DeviceStatus.Connected
    val statusColor = when (status) {
        DeviceStatus.Connected -> cs.primary
        DeviceStatus.Available -> cs.tertiary
        DeviceStatus.Remembered -> cs.onSurfaceVariant
    }
    ModernCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onClick(device.id) },
        container = if (connected) cs.secondaryContainer else cs.surfaceContainerHigh,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (device.icon > 0) {
                TonalAvatar(
                    iconRes = device.icon,
                    container = if (connected) cs.primary else cs.surfaceContainerHighest,
                    tint = if (connected) cs.onPrimary else cs.onSurfaceVariant,
                )
                Spacer(Modifier.width(14.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (connected) cs.onSecondaryContainer else cs.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(
                            when (status) {
                                DeviceStatus.Connected -> R.string.device_status_connected
                                DeviceStatus.Available -> R.string.device_status_available
                                DeviceStatus.Remembered -> R.string.device_status_remembered
                            }
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        maxLines = 1,
                    )
                }
                if (device.summaryRes > 0) {
                    Text(
                        text = stringResource(id = device.summaryRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = cs.error,
                        maxLines = 1,
                    )
                }
            }
            when (status) {
                DeviceStatus.Connected -> Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = cs.onSecondaryContainer.copy(alpha = 0.7f),
                )
                DeviceStatus.Available -> StatusPill(
                    text = stringResource(R.string.device_action_pair),
                    container = cs.primaryContainer,
                    content = cs.onPrimaryContainer,
                )
                DeviceStatus.Remembered -> Unit
            }
        }
    }
}

@KdeThemePreviews
@Composable
private fun PreviewCompose() {
    KdeTheme(context = LocalContext.current) {
        val lazyListState = rememberLazyListState()
        val pullRefreshState = rememberPullToRefreshState()

        PairingScreenContent(
            lazyListState = lazyListState,
            pullRefreshState = pullRefreshState,
            uiState = PairingUiState(
                isWifiAvailable = true,
                hasNotificationsPermission = true,
                hasLocalNetworkPermission = true,
                isTrustedNetwork = true,
                hasDuplicateNames = true,
                connected = emptyList(),
                available = listOf(
                    DeviceUiModel(
                        id = "_2504584b_6aa2_3cd6_bd1b_5e958aa6cd23_",
                        icon = R.drawable.ic_device_laptop_32dp,
                        name = "Device 1",
                        summaryRes = 0,
                        isReachable = true,
                        isPaired = false
                    ), DeviceUiModel(
                        id = "_2504584b_6aa2_3cd6_bd1b_5e958aa6cd24_",
                        icon = R.drawable.ic_device_desktop_32dp,
                        name = "Device 2",
                        summaryRes = R.string.protocol_version_newer,
                        isReachable = true,
                        isPaired = false
                    )
                ),
                remembered = emptyList(),
                isRefreshing = false
            ),
            onClick = { /* Do nothing */ },
            onWifiSettingsClick = { /* Do nothing */ },
            onNotificationSettingsClick = { /* Do nothing */ },
            onLocalNetworkPermissionClick = { /* Do nothing */ },
            onRefresh = { /* Do nothing */ }
        )
    }
}
