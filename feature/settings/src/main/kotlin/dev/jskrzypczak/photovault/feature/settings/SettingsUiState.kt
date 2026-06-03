package dev.jskrzypczak.photovault.feature.settings

enum class ThemeMode { LIGHT, DARK }

enum class AccentColor { PURPLE, TEAL, GREEN }

enum class AppLanguage { PL, EN }

data class SettingsUiState(
    val serverAddress: String = "",
    val isConnected: Boolean = false,
    val photoCount: Int = 0,
    val authSummary: String = "",
    val targetFolder: String = "",
    val autoUpload: Boolean = true,
    val wifiOnly: Boolean = true,
    val autoTagging: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val accent: AccentColor = AccentColor.PURPLE,
    val gridColumns: Int = 3,
    val language: AppLanguage = AppLanguage.PL,
    val appVersion: String = "2.4.1",
)
