package dev.jskrzypczak.photovault.core.data.repository

import app.cash.turbine.test
import dev.jskrzypczak.photovault.core.data.fakes.FakeTagDao
import dev.jskrzypczak.photovault.core.data.fakes.FakeTagsApi
import dev.jskrzypczak.photovault.core.data.fixtures.TestAppDispatchers
import dev.jskrzypczak.photovault.core.database.entity.TagEntity
import dev.jskrzypczak.photovault.core.domain.id.TagId
import dev.jskrzypczak.photovault.core.domain.model.Tag
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TagRepositoryImplTest {

    @Test
    fun `observeAll emits tags from DAO mapped to domain`() = runTest {
        val dao = FakeTagDao().apply {
            items.value = listOf(
                TagEntity(id = "tag-001", name = "#morze", photoCount = 5),
                TagEntity(id = "tag-002", name = "#zachod", photoCount = 0),
            )
        }
        val repo = TagRepositoryImpl(tagDao = dao, tagsApi = FakeTagsApi(), dispatchers = TestAppDispatchers())

        repo.observeAll().test {
            assertEquals(
                listOf(
                    Tag(id = TagId("tag-001"), name = "#morze", photoCount = 5),
                    Tag(id = TagId("tag-002"), name = "#zachod", photoCount = 0),
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
