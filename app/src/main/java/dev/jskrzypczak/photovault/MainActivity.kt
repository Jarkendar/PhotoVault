package dev.jskrzypczak.photovault

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.component.gallery.GalleryDestination
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import dev.jskrzypczak.photovault.feature.auth.LoginScreen
import dev.jskrzypczak.photovault.feature.auth.LoginViewModel
import dev.jskrzypczak.photovault.feature.gallery.GalleryScreen
import dev.jskrzypczak.photovault.feature.gallery.GalleryViewModel
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailScreen
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailUiState
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailViewModel
import dev.jskrzypczak.photovault.feature.manage.ManageScreen
import dev.jskrzypczak.photovault.feature.manage.ManageTab
import dev.jskrzypczak.photovault.feature.manage.ManageUiState
import dev.jskrzypczak.photovault.feature.search.SearchScreen
import dev.jskrzypczak.photovault.feature.search.SearchViewModel
import dev.jskrzypczak.photovault.feature.settings.SettingsScreen
import dev.jskrzypczak.photovault.feature.settings.SettingsViewModel
import dev.jskrzypczak.photovault.feature.upload.UploadScreen
import dev.jskrzypczak.photovault.feature.upload.UploadViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoVaultTheme {
                val gateViewModel = koinViewModel<AuthGateViewModel>()
                val authState by gateViewModel.authState.collectAsStateWithLifecycle()

                when (authState) {
                    AuthState.Unknown -> {
                        // Waiting for refreshSession() to complete.
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    AuthState.Unauthenticated -> {
                        val loginVm = koinViewModel<LoginViewModel>()
                        val loginState by loginVm.uiState.collectAsStateWithLifecycle()
                        LoginScreen(
                            state = loginState,
                            onUsernameChange = loginVm::onUsernameChange,
                            onPasswordChange = loginVm::onPasswordChange,
                            onLogin = loginVm::onLogin,
                        )
                    }
                    is AuthState.Authenticated -> {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = Route.GALLERY,
                        ) {
                            composable(Route.GALLERY) {
                                val viewModel = koinViewModel<GalleryViewModel>()
                                val state by viewModel.uiState.collectAsStateWithLifecycle()
                                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                                val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

                                // Silently refresh whenever the user returns to this tab (e.g. after uploading).
                                LifecycleResumeEffect(Unit) {
                                    viewModel.onAutoRefresh()
                                    onPauseOrDispose { }
                                }
                                // Start/stop periodic categorisation refresh based on app visibility.
                                LifecycleStartEffect(Unit) {
                                    viewModel.onScreenVisible()
                                    onStopOrDispose { viewModel.onScreenHidden() }
                                }

                                GalleryScreen(
                                    state = state,
                                    searchQuery = searchQuery,
                                    isRefreshing = isRefreshing,
                                    onSearchQueryChange = viewModel::onSearchQueryChange,
                                    onCategorySelect = viewModel::onCategorySelect,
                                    onPageClick = viewModel::onPageClick,
                                    onFavoriteClick = viewModel::onFavoriteClick,
                                    onPhotoClick = { photo -> navController.navigate(Route.detail(photo.id.value)) },
                                    onRefresh = viewModel::onRefresh,
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
                            composable(Route.MANAGE) {
                                // TODO(etap-8+): replace static state with koinViewModel + repository wiring
                                ManageScreen(
                                    state = ManageUiState.Content(
                                        selectedTab = ManageTab.CATEGORIES,
                                        categories = listOf(
                                            Category(id = CategoryId("c1"), name = "Natura", colorHex = "#4CAF50", photoCount = 48),
                                            Category(id = CategoryId("c2"), name = "Ludzie", colorHex = "#E91E63", photoCount = 73),
                                            Category(id = CategoryId("c3"), name = "Podróże", colorHex = "#2196F3", photoCount = 124),
                                        ).toImmutableList(),
                                        tags = listOf(
                                            Tag(id = TagId("t1"), name = "#morze", photoCount = 48),
                                        ).toImmutableList(),
                                        labels = persistentListOf(),
                                    ),
                                    onBack = { navController.navigateUp() },
                                    onDestinationSelect = { navController.navigateTab(it.route) },
                                )
                            }
                            composable(Route.SETTINGS) {
                                val viewModel = koinViewModel<SettingsViewModel>()
                                val state by viewModel.uiState.collectAsStateWithLifecycle()
                                SettingsScreen(
                                    state = state,
                                    onBack = { navController.navigateUp() },
                                    onLogout = viewModel::onLogout,
                                    onServerUrlChange = viewModel::onServerUrlChange,
                                    onDestinationSelect = { navController.navigateTab(it.route) },
                                )
                            }
                            composable(
                                route = Route.DETAIL,
                                arguments = listOf(navArgument("photoId") { type = NavType.StringType }),
                            ) {
                                val viewModel = koinViewModel<PhotoDetailViewModel>()
                                val state by viewModel.uiState.collectAsStateWithLifecycle()
                                val context = LocalContext.current
                                PhotoDetailScreen(
                                    state = state,
                                    onBack = { navController.navigateUp() },
                                    onFavoriteToggle = viewModel::onFavoriteToggle,
                                    onShare = {
                                        (state as? PhotoDetailUiState.Content)
                                            ?.photo
                                            ?.let { sharePhoto(context, it) }
                                    },
                                    onOpenInBrowser = {
                                        (state as? PhotoDetailUiState.Content)
                                            ?.photo
                                            ?.let { openInBrowser(context, it) }
                                    },
                                )
                            }
                        }
                    }
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
    const val DETAIL = "gallery_detail/{photoId}"
    fun detail(photoId: String) = "gallery_detail/$photoId"
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

private fun sharePhoto(context: Context, photo: Photo) {
    val url = photo.originalUrl.ifEmpty { photo.mediumUrl }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(dev.jskrzypczak.photovault.feature.gallery.R.string.feature_gallery_detail_share_chooser),
        ),
    )
}

private fun openInBrowser(context: Context, photo: Photo) {
    val url = photo.originalUrl.ifEmpty { photo.mediumUrl }
    if (url.isNotEmpty()) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}
