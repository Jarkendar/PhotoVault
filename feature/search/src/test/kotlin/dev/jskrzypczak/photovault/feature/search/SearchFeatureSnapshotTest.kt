package dev.jskrzypczak.photovault.feature.search

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.PhotoId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.GeoLocation
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Photo
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.domain.query.MatchMode
import dev.jskrzypczak.photovault.core.domain.query.SearchQuery
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test
import kotlin.time.Instant

class SearchFeatureSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    // ── Light mode snapshots ───────────────────────────────────────────────

    @Test fun searchScreenIdle() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SearchScreen(
                    state = SearchUiState.Idle,
                    filterPanelState = FilterPanelState(),
                    searchText = "",
                )
            }
        }
    }

    @Test fun searchScreenWithSuggestions() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SearchScreen(
                    state = SearchUiState.Idle,
                    filterPanelState = FilterPanelState(
                        suggestions = persistentListOf(
                            Tag(id = TagId("t1"), name = "#morze", photoCount = 48),
                            Tag(id = TagId("t2"), name = "#morska-bryza", photoCount = 5),
                        ),
                    ),
                    searchText = "morze",
                )
            }
        }
    }

    @Test fun searchScreenLoading() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SearchScreen(
                    state = SearchUiState.Loading,
                    filterPanelState = FilterPanelState(),
                    searchText = "morze",
                )
            }
        }
    }

    @Test fun searchScreenContent() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SearchScreen(
                    state = SearchUiState.Content(
                        photos = buildSamplePhotos(),
                        resultCount = 2,
                    ),
                    filterPanelState = FilterPanelState(),
                    searchText = "morze",
                )
            }
        }
    }

    @Test fun searchScreenEmpty() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                SearchScreen(
                    state = SearchUiState.Empty,
                    filterPanelState = FilterPanelState(),
                    searchText = "xyzzy",
                )
            }
        }
    }

    // ── Dark mode snapshots ───────────────────────────────────────────────

    @Test fun searchScreenIdleDark() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                SearchScreen(
                    state = SearchUiState.Idle,
                    filterPanelState = FilterPanelState(),
                    searchText = "",
                )
            }
        }
    }

    @Test fun searchScreenContentDark() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                SearchScreen(
                    state = SearchUiState.Content(
                        photos = buildSamplePhotos(),
                        resultCount = 2,
                    ),
                    filterPanelState = FilterPanelState(
                        availableCategories = persistentListOf(
                            Category(id = CategoryId("c1"), name = "Natura", colorHex = "#FF8B45", photoCount = 48),
                        ),
                        draftQuery = SearchQuery(
                            matchMode = MatchMode.ALL,
                        ),
                    ),
                    searchText = "morze",
                )
            }
        }
    }

    // ─── Fixtures ─────────────────────────────────────────────────────────

    private fun buildSamplePhotos() = List(4) { i ->
        Photo(
            id = PhotoId("p$i"),
            name = "photo_$i.jpg",
            sizeBytes = 1_024_000L,
            mimeType = "image/jpeg",
            width = 800 + i * 50,
            height = 600 + i * 30,
            capturedAt = null,
            uploadedAt = Instant.fromEpochMilliseconds(0L),
            camera = null,
            location = null,
            tags = persistentListOf(),
            categories = persistentListOf(),
            labels = persistentListOf(),
            isFavorite = i % 2 == 0,
            thumbnailUrl = "", mediumUrl = "", originalUrl = "",
        )
    }.toImmutableList()
}
