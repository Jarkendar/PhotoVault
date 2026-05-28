package dev.jarkendar.photovault.core.data.repository

import app.cash.turbine.test
import dev.jarkendar.photovault.core.data.fakes.FakeCategoryDao
import dev.jarkendar.photovault.core.data.fixtures.TestAppDispatchers
import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.domain.id.CategoryId
import dev.jarkendar.photovault.core.domain.model.Category
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryRepositoryImplTest {

    @Test
    fun `observeAll emits categories from DAO mapped to domain`() = runTest {
        val dao = FakeCategoryDao().apply {
            items.value = listOf(CategoryEntity(id = "cat-001", name = "Natura", colorHex = "#FF8B45"))
        }
        val repo = CategoryRepositoryImpl(categoryDao = dao, dispatchers = TestAppDispatchers())

        repo.observeAll().test {
            assertEquals(
                listOf(Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45")),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
