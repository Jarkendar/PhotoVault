package dev.jskrzypczak.photovault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.gallery.GalleryScreen
import dev.jskrzypczak.photovault.feature.gallery.GalleryViewModel
import dev.jskrzypczak.photovault.feature.search.SearchScreen
import dev.jskrzypczak.photovault.feature.search.SearchViewModel
import dev.jskrzypczak.photovault.feature.upload.UploadScreen
import dev.jskrzypczak.photovault.feature.upload.UploadViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoVaultTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Route.GALLERY,
                ) {
                    composable(Route.GALLERY) {
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
                            onUploadClick = { navController.navigateTab(Route.UPLOAD) },
                            onDestinationSelect = { navController.navigateTab(it.route) },
                        )
                    }
                    composable(Route.UPLOAD) {
                        val viewModel = koinViewModel<UploadViewModel>()
                        val state by viewModel.uiState.collectAsStateWithLifecycle()
                        UploadScreen(
                            state = state,
                            onBack = { navController.navigateUp() },
                            onPhotosSelected = viewModel::onPhotosSelected,
                            onToggleAutoDetect = viewModel::onToggleAutoDetect,
                            onCancelUpload = viewModel::onCancelUpload,
                            onDestinationSelect = { navController.navigateTab(it.route) },
                        )
                    }
                    composable(Route.SEARCH) {
                        val viewModel = koinViewModel<SearchViewModel>()
                        val state by viewModel.uiState.collectAsStateWithLifecycle()
                        val filterPanelState by viewModel.filterPanelState.collectAsStateWithLifecycle()
                        val searchText by viewModel.searchText.collectAsStateWithLifecycle()
                        SearchScreen(
                            state = state,
                            filterPanelState = filterPanelState,
                            searchText = searchText,
                            onSearchTextChange = viewModel::onSearchTextChange,
                            onBack = { navController.navigateUp() },
                            onToggleCategory = viewModel::onToggleCategory,
                            onToggleTag = viewModel::onToggleTag,
                            onToggleLabel = viewModel::onToggleLabel,
                            onMatchModeChange = viewModel::onMatchModeChange,
                            onClearFilters = viewModel::onClearFilters,
                            onApplyFilters = viewModel::onApplyFilters,
                            onDestinationSelect = { navController.navigateTab(it.route) },
                        )
                    }
                    composable(Route.MANAGE) { PlaceholderScreen() }
                    composable(Route.SETTINGS) { PlaceholderScreen() }
                }
            }
        }
    }
}

private object Route {
    const val GALLERY = "gallery"
    const val SEARCH = "search"
    const val UPLOAD = "upload"
    const val MANAGE = "manage"
    const val SETTINGS = "settings"
}

private val GalleryDestination.route: String
    get() = when (this) {
        GalleryDestination.GALLERY -> Route.GALLERY
        GalleryDestination.SEARCH -> Route.SEARCH
        GalleryDestination.UPLOAD -> Route.UPLOAD
        GalleryDestination.MANAGE -> Route.MANAGE
        GalleryDestination.SETTINGS -> Route.SETTINGS
    }

private fun NavController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(Route.GALLERY) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun PlaceholderScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Coming soon",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
