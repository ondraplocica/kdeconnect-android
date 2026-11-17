/*
 * SPDX-FileCopyrightText: 2026 Ondrej Plocica
 *
 * SPDX-License-Identifier: GPL-2.0-only OR GPL-3.0-only OR LicenseRef-KDE-Accepted-GPL
 */

package org.kde.kdeconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.kde.kdeconnect.plugins.Plugin
import org.kde.kdeconnect_tp.R
import org.kde.kdeconnect.ui.compose.KdeTheme
import org.kde.kdeconnect.ui.compose.model.device.DeviceUiModel
import org.kde.kdeconnect.ui.compose.screen.device.DeviceErrorScreen
import org.kde.kdeconnect.ui.compose.screen.device.DevicePairingScreen
import org.kde.kdeconnect.ui.compose.screen.device.PluginsScreen
import org.kde.kdeconnect.ui.compose.screen.pairing.PairingScreen
import org.kde.kdeconnect.ui.compose.screen.pairing.PairingUiState

/**
 * Debug-only: shows the app's Compose screens with sample data so a redesign can be checked without
 * a paired device.  adb shell am start -n org.kde.kdeconnect_tp.debug/org.kde.kdeconnect.DesignGalleryActivity --es screen list
 * screens: list | empty | device | pairing | requested | error
 */
class DesignGalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val screen = intent.getStringExtra("screen") ?: "list"
        setContent {
            KdeTheme(this) {
                Surface(Modifier.fillMaxSize().systemBarsPadding()) {
                    when (screen) {
                        "list" -> PairingScreen(uiState = sample(), onClick = {})
                        "empty" -> PairingScreen(uiState = sample().copy(connected = emptyList(), available = emptyList(), remembered = emptyList()), onClick = {})
                        "device" -> PluginsScreen(
                            deviceName = "Pixel 9 Pro XL",
                            deviceIcon = R.drawable.ic_device_phone_32dp,
                            pluginsWithButtons = buttons(),
                            pluginsNeedPermissions = emptyList(),
                            pluginsNeedOptionalPermissions = emptyList(),
                            onButtonClick = {},
                            actionNeedPermissions = {},
                            actionNeedOptionalPermissions = {},
                        )
                        "pairing" -> DevicePairingScreen(
                            pairStatus = org.kde.kdeconnect.PairingHandler.PairState.NotPaired,
                            verificationKey = "", onRequestPairing = {}, onAcceptPairing = {}, onRejectPairing = {},
                        )
                        "requested" -> DevicePairingScreen(
                            pairStatus = org.kde.kdeconnect.PairingHandler.PairState.RequestedByPeer,
                            verificationKey = "a7f3 91c2", onRequestPairing = {}, onAcceptPairing = {}, onRejectPairing = {},
                        )
                        else -> DeviceErrorScreen(message = getString(R.string.unreachable_description), isRefreshing = false, onRefresh = {})
                    }
                }
            }
        }
    }

    private fun sample() = PairingUiState(
        isWifiAvailable = true, hasNotificationsPermission = true, hasLocalNetworkPermission = true,
        isTrustedNetwork = true, hasDuplicateNames = false,
        connected = listOf(
            DeviceUiModel("1", R.drawable.ic_device_phone_32dp, "Pixel 9 Pro XL", 0, isReachable = true, isPaired = true),
            DeviceUiModel("2", R.drawable.ic_device_desktop_32dp, "Ondra-PC", 0, isReachable = true, isPaired = true),
        ),
        available = listOf(DeviceUiModel("3", R.drawable.ic_device_laptop_32dp, "Fedora laptop", 0, isReachable = true, isPaired = false)),
        remembered = listOf(
            DeviceUiModel("4", R.drawable.ic_device_tablet_32dp, "Galaxy Tab S9", 0, isReachable = false, isPaired = true),
            DeviceUiModel("5", R.drawable.ic_device_tv_32dp, "Living room TV", 0, isReachable = false, isPaired = true),
        ),
    )

    private fun buttons() = listOf(
        Plugin.PluginUiButton("Send files", R.drawable.share_plugin_action_24dp) {},
        Plugin.PluginUiButton("Send clipboard", R.drawable.ic_baseline_content_paste_24) {},
        Plugin.PluginUiButton("Multimedia controls", R.drawable.mpris_plugin_action_24dp) {},
        Plugin.PluginUiButton("Remote input", R.drawable.touchpad_plugin_action_24dp) {},
        Plugin.PluginUiButton("Run command", R.drawable.run_command_plugin_icon_24dp) {},
        Plugin.PluginUiButton("Slideshow remote", R.drawable.ic_presenter_24dp) {},
    )
}
