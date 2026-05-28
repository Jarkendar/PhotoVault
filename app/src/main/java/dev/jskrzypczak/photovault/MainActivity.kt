package dev.jskrzypczak.photovault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.jarkendar.photovault.core.ui.theme.PhotoVaultTheme
import dev.jarkendar.photovault.feature.gallery.GalleryScreen
import dev.jarkendar.photovault.feature.gallery.GalleryUiState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoVaultTheme {
                GalleryScreen(
                    state = GalleryUiState.Loading,
                )
            }
        }
    }
}
