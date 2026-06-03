package dev.jskrzypczak.photovault.feature.settings.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.jskrzypczak.photovault.feature.settings.R

/**
 * Dialog for editing the backend server base URL.
 * Shows an [OutlinedTextField] pre-filled with [currentUrl].
 * Validates that the value starts with "http://" or "https://" before calling [onConfirm].
 */
@Composable
fun EditServerDialog(
    currentUrl: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf(currentUrl) }
    val isError = text.isNotEmpty() &&
        !text.startsWith("http://") &&
        !text.startsWith("https://")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.feature_settings_dialog_server_title)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.feature_settings_dialog_server_label)) },
                placeholder = { Text(stringResource(R.string.feature_settings_dialog_server_hint)) },
                isError = isError,
                supportingText = if (isError) {
                    { Text(stringResource(R.string.feature_settings_dialog_server_error)) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (!isError && text.isNotEmpty()) onConfirm(text) },
                enabled = text.isNotEmpty() && !isError,
            ) {
                Text(stringResource(R.string.feature_settings_dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.feature_settings_dialog_cancel),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}
