package dev.jskrzypczak.photovault.feature.manage

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Category
import dev.jskrzypczak.photovault.core.domain.model.Label
import dev.jskrzypczak.photovault.core.domain.model.Tag
import dev.jskrzypczak.photovault.core.ui.theme.PhotoVaultTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.junit.Rule
import org.junit.Test

class ManageFeatureSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6)

    // ── Light mode snapshots ───────────────────────────────────────────────

    @Test fun manageScreenCategories() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                ManageScreen(state = buildCategoriesContent())
            }
        }
    }

    @Test fun manageScreenTags() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                ManageScreen(state = buildTagsContent())
            }
        }
    }

    @Test fun manageScreenLabels() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                ManageScreen(state = buildLabelsContent())
            }
        }
    }

    @Test fun manageScreenLoading() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                ManageScreen(state = ManageUiState.Loading)
            }
        }
    }

    @Test fun manageScreenEmpty() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false) {
                ManageScreen(state = ManageUiState.Empty)
            }
        }
    }

    // ── Dark mode snapshots ───────────────────────────────────────────────

    @Test fun manageScreenCategoriesDark() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                ManageScreen(state = buildCategoriesContent())
            }
        }
    }

    @Test fun manageScreenTagsDark() {
        paparazzi.snapshot {
            PhotoVaultTheme(dynamicColor = false, darkTheme = true) {
                ManageScreen(state = buildTagsContent())
            }
        }
    }

    // ─── Fixtures ─────────────────────────────────────────────────────────

    private fun buildCategoriesContent() = ManageUiState.Content(
        selectedTab = ManageTab.CATEGORIES,
        categories = listOf(
            Category(id = CategoryId("c1"), name = "Natura", colorHex = "#4CAF50", photoCount = 48),
            Category(id = CategoryId("c2"), name = "Ludzie", colorHex = "#E91E63", photoCount = 73),
            Category(id = CategoryId("c3"), name = "Podróże", colorHex = "#2196F3", photoCount = 124),
            Category(id = CategoryId("c4"), name = "Jedzenie", colorHex = "#FF9800", photoCount = 31),
            Category(id = CategoryId("c5"), name = "Architektura", colorHex = "#9C27B0", photoCount = 56),
            Category(id = CategoryId("c6"), name = "Zwierzęta", colorHex = "#9E9E9E", photoCount = 22),
            Category(id = CategoryId("c7"), name = "Wydarzenia", colorHex = "#00BCD4", photoCount = 19),
        ).toImmutableList(),
        tags = persistentListOf(),
        labels = persistentListOf(),
    )

    private fun buildTagsContent() = ManageUiState.Content(
        selectedTab = ManageTab.TAGS,
        categories = persistentListOf(),
        tags = listOf(
            Tag(id = TagId("t1"), name = "#morze", photoCount = 48),
            Tag(id = TagId("t2"), name = "#góry", photoCount = 35),
            Tag(id = TagId("t3"), name = "#miasto", photoCount = 92),
            Tag(id = TagId("t4"), name = "#rodzina", photoCount = 67),
            Tag(id = TagId("t5"), name = "#wakacje", photoCount = 54),
        ).toImmutableList(),
        labels = persistentListOf(),
    )

    private fun buildLabelsContent() = ManageUiState.Content(
        selectedTab = ManageTab.LABELS,
        categories = persistentListOf(),
        tags = persistentListOf(),
        labels = listOf(
            Label(id = LabelId("l1"), name = "Ulubione", colorHex = "#F44336", photoCount = 28),
            Label(id = LabelId("l2"), name = "Do usunięcia", colorHex = "#FF9800", photoCount = 12),
            Label(id = LabelId("l3"), name = "Prywatne", colorHex = "#3F51B5", photoCount = 45),
        ).toImmutableList(),
    )
}
