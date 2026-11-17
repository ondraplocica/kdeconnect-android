/*
 * SPDX-FileCopyrightText: 2026 Saul Cintero Chocarro <scintero@gmail.com>
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect.ui.compose.screen.device

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import org.kde.kdeconnect.ui.compose.components.ModernCard
import org.kde.kdeconnect.ui.compose.components.StatusPill
import org.kde.kdeconnect.ui.compose.components.TonalAvatar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.kde.kdeconnect.extensions.safeDrawingBottomPadding
import org.kde.kdeconnect.plugins.Plugin
import org.kde.kdeconnect.plugins.clipboard.ClipboardPlugin
import org.kde.kdeconnect.plugins.mpris.MprisPlugin
import org.kde.kdeconnect.plugins.ping.PingPlugin
import org.kde.kdeconnect.plugins.presenter.PresenterPlugin
import org.kde.kdeconnect.plugins.runcommand.RunCommandPlugin
import org.kde.kdeconnect.ui.compose.KdeTheme
import org.kde.kdeconnect.ui.compose.components.KdeThemePreviews
import org.kde.kdeconnect_tp.R

@Composable
fun PluginsScreen(
    pluginsWithButtons: List<Plugin.PluginUiButton>,
    pluginsNeedPermissions: List<Plugin>,
    pluginsNeedOptionalPermissions: List<Plugin>,
    onButtonClick: (Plugin.PluginUiButton) -> Unit,
    actionNeedPermissions: (plugin: Plugin) -> Unit,
    actionNeedOptionalPermissions: (plugin: Plugin) -> Unit,
    deviceName: String? = null,
    deviceIcon: Int = 0,
) {
    PluginsScreenContent(
        deviceName = deviceName,
        deviceIcon = deviceIcon,
        pluginsWithButtons = pluginsWithButtons,
        pluginsNeedPermissions = pluginsNeedPermissions,
        pluginsNeedOptionalPermissions = pluginsNeedOptionalPermissions,
        onButtonClick = onButtonClick,
        actionNeedPermissions = actionNeedPermissions,
        actionNeedOptionalPermissions = actionNeedOptionalPermissions,
    )
}

@Composable
private fun PluginsScreenContent(
    deviceName: String? = null,
    deviceIcon: Int = 0,
    pluginsWithButtons: List<Plugin.PluginUiButton>,
    pluginsNeedPermissions: List<Plugin>,
    pluginsNeedOptionalPermissions: List<Plugin>,
    onButtonClick: (Plugin.PluginUiButton) -> Unit,
    actionNeedPermissions: (plugin: Plugin) -> Unit,
    actionNeedOptionalPermissions: (plugin: Plugin) -> Unit,
) {
    val bottomPadding = safeDrawingBottomPadding().calculateBottomPadding()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
                .padding(top = 16.dp, bottom = bottomPadding)
        ) {
            if (deviceName != null) {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) { DeviceHeader(name = deviceName, icon = deviceIcon) }
                Spacer(modifier = Modifier.height(16.dp))
            }
            val numColumns = LocalResources.current.getInteger(R.integer.plugins_columns)

            PluginButtons(
                buttons = pluginsWithButtons,
                numColumns = numColumns,
                onButtonClick = onButtonClick
            )
            Spacer(modifier = Modifier.padding(vertical = 6.dp))
            if (pluginsNeedPermissions.isNotEmpty()) {
                PluginsWithoutPermissions(
                    title = stringResource(id = R.string.plugins_need_permission),
                    plugins = pluginsNeedPermissions,
                    action = actionNeedPermissions,
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
            }
            if (pluginsNeedOptionalPermissions.isNotEmpty()) {
                PluginsWithoutPermissions(
                    title = stringResource(id = R.string.plugins_need_optional_permission),
                    plugins = pluginsNeedOptionalPermissions,
                    action = actionNeedOptionalPermissions
                )
            }
        }
    }
}

@Composable
private fun PluginButtons(
    buttons: List<Plugin.PluginUiButton>,
    numColumns: Int,
    onButtonClick: (Plugin.PluginUiButton) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        val buttonIter = buttons.iterator()

        while (buttonIter.hasNext()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                repeat(times = numColumns) {
                    if (buttonIter.hasNext()) {
                        val button = buttonIter.next()

                        PluginButton(
                            button = button,
                            modifier = Modifier.weight(1f),
                            onClick = { onButtonClick(button) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceHeader(name: String, icon: Int) {
    val cs = MaterialTheme.colorScheme
    ModernCard(
        container = cs.primaryContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon > 0) {
                TonalAvatar(iconRes = icon, size = 64.dp, container = cs.primary, tint = cs.onPrimary)
                Spacer(Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.headlineSmall, color = cs.onPrimaryContainer, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                StatusPill(
                    text = stringResource(R.string.device_status_connected),
                    container = cs.onPrimaryContainer.copy(alpha = 0.12f),
                    content = cs.onPrimaryContainer,
                    dot = true,
                )
            }
        }
    }
}

@Composable
private fun PluginButton(
    button: Plugin.PluginUiButton,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    ModernCard(
        modifier = modifier.semantics { role = Role.Button },
        onClick = onClick,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            TonalAvatar(iconRes = button.iconRes, size = 46.dp, container = cs.secondaryContainer, tint = cs.onSecondaryContainer)
            Text(
                text = button.name,
                style = MaterialTheme.typography.titleSmall,
                color = cs.onSurface,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PluginsWithoutPermissions(
    title: String,
    plugins: Collection<Plugin>,
    action: (plugin: Plugin) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = cs.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp).semantics { heading() }
        )
        plugins.forEach { plugin ->
            ModernCard(
                onClick = { action(plugin) },
                modifier = Modifier.fillMaxWidth().semantics { role = Role.Button },
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TonalAvatar(iconRes = R.drawable.ic_warning, size = 38.dp, container = cs.errorContainer, tint = cs.onErrorContainer)
                    Spacer(Modifier.width(12.dp))
                    Text(text = plugin.displayName, style = MaterialTheme.typography.bodyLarge, color = cs.onSurface, modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = cs.onSurfaceVariant)
                }
            }
        }
    }
}

@KdeThemePreviews
@Composable
private fun PluginsScreenPreview() {
    KdeTheme(context = LocalContext.current) {
        val mprisPlugin = MprisPlugin().also { it.setContext(LocalContext.current, null) }
        val runCommandPlugin = RunCommandPlugin().also { it.setContext(LocalContext.current, null) }
        val presenterPlugin = PresenterPlugin().also { it.setContext(LocalContext.current, null) }
        val pingPlugin = PingPlugin().also { it.setContext(LocalContext.current, null) }
        val clipboardPlugin = ClipboardPlugin().also { it.setContext(LocalContext.current, null) }

        val pluginsWithButtons = listOf(pingPlugin, mprisPlugin, presenterPlugin, runCommandPlugin)
        val pluginsWithoutPermissions = listOf(clipboardPlugin, presenterPlugin)
        val pluginsWithoutOptionalPermissions = listOf(mprisPlugin, presenterPlugin)

        PluginsScreenContent(
            deviceName = "Pixel 9 Pro XL",
            deviceIcon = R.drawable.ic_device_phone_32dp,
            pluginsWithButtons = pluginsWithButtons.flatMap { plugin -> plugin.getUiButtons() },
            pluginsNeedPermissions = pluginsWithoutPermissions,
            pluginsNeedOptionalPermissions = pluginsWithoutOptionalPermissions,
            onButtonClick = { /* Do nothing */ },
            actionNeedPermissions = { /* Do nothing */ },
            actionNeedOptionalPermissions = { /* Do nothing */ },
        )
    }
}
