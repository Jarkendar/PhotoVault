package dev.jskrzypczak.photovault.feature.upload

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.ui.component.gallery.AppBottomNavBar
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.component.upload.ActiveUploadBar
import dev.jskrzypczak.photovault.core.ui.component.upload.AutoDetectionBanner
import dev.jskrzypczak.photovault.core.ui.component.upload.UploadStatsRow
import dev.jskrzypczak.photovault.feature.upload.component.UploadQueueItem
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    state: UploadUiState,
    onBack: () -> Unit = {},
    onPhotosSelected: (List<android.net.Uri>) -> Unit = {},
    onToggleAutoDetect: (Boolean) -> Unit = {},
    onCancelUpload: (UUID) -> Unit = {},
    onApplyTagsToAll: () -> Unit = {},
    onDestinationSelect: (GalleryDestination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> if (uris.isNotEmpty()) onPhotosSelected(uris) },
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted && state.autoDetectEnabled) onToggleAutoDetect(true)
        },
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.feature_upload_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.feature_upload_title),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.feature_upload_refresh),
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.feature_upload_more),
                        )
                    }
                },
            )
        },
        bottomBar = {
            AppBottomNavBar(
                selectedDestination = GalleryDestination.UPLOAD,
                onSelect = onDestinationSelect,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    pickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.feature_upload_add_photos),
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                AutoDetectionBanner(
                    title = stringResource(R.string.feature_upload_auto_detect_title),
                    subtitle = if (state.newPhotosDetected > 0) {
                        stringResource(R.string.feature_upload_auto_detect_subtitle_new, state.newPhotosDetected)
                    } else {
                        stringResource(R.string.feature_upload_auto_detect_subtitle_none)
                    },
                    enabled = state.autoDetectEnabled,
                    onToggle = { enabled ->
                        if (enabled) {
                            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                            permissionLauncher.launch(permission)
                        } else {
                            onToggleAutoDetect(false)
                        }
                    },
                )
            }

            item {
                UploadStatsRow(
                    uploadingCount = state.uploadingCount,
                    queuedCount = state.queuedCount,
                    doneCount = state.doneCount,
                    uploadingLabel = stringResource(R.string.feature_upload_stat_uploading),
                    queuedLabel = stringResource(R.string.feature_upload_stat_queued),
                    doneLabel = stringResource(R.string.feature_upload_stat_done),
                )
            }

            if (state.activeUpload != null) {
                item {
                    ActiveUploadBar(
                        fileName = state.activeUpload.fileName,
                        progress = state.activeUpload.progress,
                    )
                }
            }

            if (state.uploads.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.feature_upload_section_review),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        TextButton(onClick = onApplyTagsToAll) {
                            Text(stringResource(R.string.feature_upload_apply_to_all))
                        }
                    }
                }

                items(
                    items = state.uploads,
                    key = { it.workId },
                ) { item ->
                    UploadQueueItem(
                        item = item,
                        onCancel = onCancelUpload,
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}
