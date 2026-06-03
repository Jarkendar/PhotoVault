package dev.jskrzypczak.photovault.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.settings.component.AccentColorPicker
import dev.jskrzypczak.photovault.feature.settings.component.EditServerDialog
import dev.jskrzypczak.photovault.feature.settings.component.SectionHeader
import dev.jskrzypczak.photovault.feature.settings.component.SettingsCard
import dev.jskrzypczak.photovault.feature.settings.component.SettingsToggleRow

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit = {},
    onServerUrlChange: (String) -> Unit = {},
    onToggleAutoUpload: (Boolean) -> Unit = {},
    onToggleWifiOnly: (Boolean) -> Unit = {},
    onToggleAutoTagging: (Boolean) -> Unit = {},
    onThemeModeChange: (ThemeMode) -> Unit = {},
    onAccentChange: (AccentColor) -> Unit = {},
    onGridColumnsChange: (Int) -> Unit = {},
    onLanguageChange: (AppLanguage) -> Unit = {},
    onDestinationSelect: (GalleryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var showEditServerDialog by remember { mutableStateOf(false) }

    if (showEditServerDialog) {
        EditServerDialog(
            currentUrl = state.serverAddress,
            onConfirm = { newUrl ->
                onServerUrlChange(newUrl)
                showEditServerDialog = false
            },
            onDismiss = { showEditServerDialog = false },
        )
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.feature_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.feature_settings_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.SETTINGS,
                onSelect = onDestinationSelect,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── SERWER ────────────────────────────────────────────────────
            SectionHeader(title = stringResource(R.string.feature_settings_section_server))
            SettingsCard {
                // Server address row
                ServerAddressRow(
                    address = state.serverAddress,
                    isConnected = state.isConnected,
                    photoCount = state.photoCount,
                    onEditClick = { showEditServerDialog = true },
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                // Auth row
                SimpleInfoRow(
                    icon = Icons.Default.Lock,
                    title = stringResource(R.string.feature_settings_auth_title),
                    subtitle = state.authSummary,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                // Target folder row
                SimpleInfoRow(
                    icon = Icons.Default.FolderOpen,
                    title = stringResource(R.string.feature_settings_target_folder),
                    subtitle = state.targetFolder,
                )
            }

            // ── PRZEŚLIJ ──────────────────────────────────────────────────
            SectionHeader(title = stringResource(R.string.feature_settings_section_upload))
            SettingsCard {
                // TODO(etap-8+): persist via DataStore + SettingsRepository,
                // apply WorkManager Constraints (wifi-only) and auto-upload trigger.
                SettingsToggleRow(
                    icon = Icons.Default.Bolt,
                    title = stringResource(R.string.feature_settings_auto_upload_title),
                    subtitle = stringResource(R.string.feature_settings_auto_upload_subtitle),
                    checked = state.autoUpload,
                    onCheckedChange = onToggleAutoUpload,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsToggleRow(
                    icon = Icons.Default.Wifi,
                    title = stringResource(R.string.feature_settings_wifi_only_title),
                    subtitle = stringResource(R.string.feature_settings_wifi_only_subtitle),
                    checked = state.wifiOnly,
                    onCheckedChange = onToggleWifiOnly,
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                SettingsToggleRow(
                    icon = Icons.Default.SmartToy,
                    title = stringResource(R.string.feature_settings_auto_tagging_title),
                    subtitle = stringResource(R.string.feature_settings_auto_tagging_subtitle),
                    checked = state.autoTagging,
                    onCheckedChange = onToggleAutoTagging,
                )
            }

            // ── WYGLĄD ────────────────────────────────────────────────────
            SectionHeader(title = stringResource(R.string.feature_settings_section_appearance))
            SettingsCard {
                // Theme (Jasny / Ciemny)
                // TODO(etap-8+): apply theme/accent/density/language live via PhotoVaultTheme.
                AppearanceLabelRow(label = stringResource(R.string.feature_settings_theme))
                val themeModes = ThemeMode.entries
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    themeModes.forEachIndexed { index, mode ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = themeModes.size),
                            onClick = { onThemeModeChange(mode) },
                            selected = mode == state.themeMode,
                            label = {
                                Text(
                                    when (mode) {
                                        ThemeMode.LIGHT -> stringResource(R.string.feature_settings_theme_light)
                                        ThemeMode.DARK -> stringResource(R.string.feature_settings_theme_dark)
                                    }
                                )
                            },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Accent color
                AppearanceLabelRow(label = stringResource(R.string.feature_settings_accent))
                AccentColorPicker(
                    selected = state.accent,
                    onSelect = onAccentChange,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )

                Spacer(Modifier.height(4.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Grid density (2 / 3 / 4 columns)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feature_settings_density),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(R.string.feature_settings_density_columns, state.gridColumns),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    val densityOptions = listOf(2, 3, 4)
                    SingleChoiceSegmentedButtonRow {
                        densityOptions.forEachIndexed { index, cols ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = densityOptions.size),
                                onClick = { onGridColumnsChange(cols) },
                                selected = cols == state.gridColumns,
                                label = { Text(cols.toString()) },
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                // Language (PL / EN)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.feature_settings_language),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = when (state.language) {
                                AppLanguage.PL -> stringResource(R.string.feature_settings_lang_pl)
                                AppLanguage.EN -> stringResource(R.string.feature_settings_lang_en)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    val languages = AppLanguage.entries
                    SingleChoiceSegmentedButtonRow {
                        languages.forEachIndexed { index, lang ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = languages.size),
                                onClick = { onLanguageChange(lang) },
                                selected = lang == state.language,
                                label = {
                                    Text(
                                        when (lang) {
                                            AppLanguage.PL -> stringResource(R.string.feature_settings_lang_pl)
                                            AppLanguage.EN -> stringResource(R.string.feature_settings_lang_en)
                                        }
                                    )
                                },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // ── INFORMACJE ────────────────────────────────────────────────
            SectionHeader(title = stringResource(R.string.feature_settings_section_info))
            SettingsCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.feature_settings_app_name),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = stringResource(R.string.feature_settings_version, state.appVersion),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ServerAddressRow(
    address: String,
    isConnected: Boolean,
    photoCount: Int,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = address,
                style = MaterialTheme.typography.bodyLarge,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isConnected) {
                    // Green dot indicator
                    androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color(0xFF4CAF50))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.feature_settings_connected, photoCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.feature_settings_disconnected),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.feature_settings_edit_server),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SimpleInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun AppearanceLabelRow(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@PhonePreview
@Composable
private fun SettingsScreenLightPreview() {
    PhotoVaultTheme(dynamicColor = false) {
        SettingsScreen(state = SettingsUiState(themeMode = ThemeMode.LIGHT))
    }
}

@PhonePreview
@Composable
private fun SettingsScreenDarkPreview() {
    PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
        SettingsScreen(state = SettingsUiState(themeMode = ThemeMode.DARK))
    }
}
