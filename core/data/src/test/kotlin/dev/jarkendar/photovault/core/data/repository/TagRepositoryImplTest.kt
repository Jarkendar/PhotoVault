package dev.jarkendar.photovault.core.data.repository

import app.cash.turbine.test
import dev.jarkendar.photovault.core.data.fakes.FakeTagDao
import dev.jarkendar.photovault.core.data.fixtures.TestAppDispatchers
import dev.jarkendar.photovault.core.database.entity.TagEntity
import dev.jarkendar.photovault.core.domain.id.TagId
import dev.jarkendar.photovault.core.domain.model.Tag
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TagRepositoryImplTest {

    @Test
    fun `observeAll emits tags from DAO mapped to domain`() = runTest {
        val dao = FakeTagDao().apply {
            items.value = listOf(
                TagEntity(id = "tag-001", name = "#morze"),
                TagEntity(id = "tag-002", name = "#zachod"),
            )
        }
        val repo = TagRepositoryImpl(tagDao = dao, dispatchers = TestAppDispatchers())

        repo.observeAll().test {
            assertEquals(
                listOf(
                    Tag(id = TagId("tag-001"), name = "#morze"),
                    Tag(id = TagId("tag-002"), name = "#zachod"),
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
