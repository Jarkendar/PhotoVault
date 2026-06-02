package dev.jskrzypczak.photovault.feature.settings

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import org.junit.Rule
import org.junit.Test

class SettingsFeatureSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    // ── Light mode snapshots ───────────────────────────────────────────────

    @Test fun settingsScreenLight() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SettingsScreen(
                    state = SettingsUiState(themeMode = ThemeMode.LIGHT),
                )
            }
        }
    }

    @Test fun settingsScreenLightDisconnected() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SettingsScreen(
                    state = SettingsUiState(
                        isConnected = false,
                        themeMode = ThemeMode.LIGHT,
                        autoUpload = false,
                        wifiOnly = false,
                    ),
                )
            }
        }
    }

    // ── Dark mode snapshots ───────────────────────────────────────────────

    @Test fun settingsScreenDark() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                SettingsScreen(
                    state = SettingsUiState(themeMode = ThemeMode.DARK),
                )
            }
        }
    }

    @Test fun settingsScreenDarkGreenAccent() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                SettingsScreen(
                    state = SettingsUiState(
                        themeMode = ThemeMode.DARK,
                        accent = AccentColor.GREEN,
                        gridColumns = 4,
                        language = AppLanguage.EN,
                    ),
                )
            }
        }
    }
}
