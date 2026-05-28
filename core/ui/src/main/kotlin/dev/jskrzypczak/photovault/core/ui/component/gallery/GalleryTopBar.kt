package dev.jskrzypczak.photovault.core.ui.component.gallery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.jskrzypczak.photovault.core.ui.R
import dev.jskrzypczak.photovault.core.ui.preview.PhonePreview
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryTopBar(
    serverIp: String?,
    isConnected: Boolean,
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "PhotoVault")
                if (serverIp != null) {
                    ServerStatusIndicator(ip = serverIp, isConnected = isConnected)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.core_ui_menu),
                )
            }
        },
        actions = {
            IconButton(onClick = onAvatarClick) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.core_ui_account),
                )
            }
        },
        modifier = modifier,
    )
}

@PhonePreview
@Composable
private fun GalleryTopBarConnectedPreview() {
    PhotoVaultTheme {
        GalleryTopBar(serverIp = "192.168.1.42", isConnected = true, onMenuClick = {}, onAvatarClick = {})
    }
}

@PhonePreview
@Composable
private fun GalleryTopBarDisconnectedPreview() {
    PhotoVaultTheme {
        GalleryTopBar(serverIp = "192.168.1.42", isConnected = false, onMenuClick = {}, onAvatarClick = {})
    }
}
