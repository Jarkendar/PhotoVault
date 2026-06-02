package dev.jskrzypczak.photovault.feature.settings

enum class ThemeMode { LIGHT, DARK }

enum class AccentColor { PURPLE, TEAL, GREEN }

enum class AppLanguage { PL, EN }

data class SettingsUiState(
    val serverAddress: String = "192.168.1.42:8080",
    val isConnected: Boolean = true,
    val photoCount: Int = 847,
    val authSummary: String = "Token · wygasa za 24h",
    val targetFolder: String = "/storage/photos/2026",
    val autoUpload: Boolean = true,
    val wifiOnly: Boolean = true,
    val autoTagging: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val accent: AccentColor = AccentColor.PURPLE,
    val gridColumns: Int = 3,
    val language: AppLanguage = AppLanguage.PL,
    val appVersion: String = "2.4.1",
)
