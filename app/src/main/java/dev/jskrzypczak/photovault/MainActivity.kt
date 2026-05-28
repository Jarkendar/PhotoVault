package dev.jskrzypczak.photovault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.gallery.GalleryScreen
import dev.jskrzypczak.photovault.feature.gallery.GalleryViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoVaultTheme {
                val viewModel = koinViewModel<GalleryViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                GalleryScreen(
                    state = state,
                    searchQuery = searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onCategorySelect = viewModel::onCategorySelect,
                    onPageClick = viewModel::onPageClick,
                    onFavoriteClick = viewModel::onFavoriteClick,
                )
            }
        }
    }
}
