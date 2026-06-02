package dev.jskrzypczak.photovault.core.data.repository

import app.cash.turbine.test
import dev.jskrzypczak.photovault.core.data.fakes.FakeCategoriesApi
import dev.jskrzypczak.photovault.core.data.fakes.FakeCategoryDao
import dev.jskrzypczak.photovault.core.data.fixtures.TestAppDispatchers
import dev.jskrzypczak.photovault.core.database.entity.CategoryEntity
import dev.jskrzypczak.photovault.core.domain.id.CategoryId
import dev.jskrzypczak.photovault.core.domain.model.Category
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CategoryRepositoryImplTest {

    @Test
    fun `observeAll emits categories from DAO mapped to domain`() = runTest {
        val dao = FakeCategoryDao().apply {
            items.value = listOf(CategoryEntity(id = "cat-001", name = "Natura", colorHex = "#FF8B45", photoCount = 12))
        }
        val repo = CategoryRepositoryImpl(categoryDao = dao, categoriesApi = FakeCategoriesApi(), dispatchers = TestAppDispatchers())

        repo.observeAll().test {
            assertEquals(
                listOf(Category(id = CategoryId("cat-001"), name = "Natura", colorHex = "#FF8B45", photoCount = 12)),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
