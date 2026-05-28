package dev.jskrzypczak.photovault.core.data.repository

import app.cash.turbine.test
import dev.jskrzypczak.photovault.core.data.fakes.FakeLabelDao
import dev.jskrzypczak.photovault.core.data.fixtures.TestAppDispatchers
import dev.jskrzypczak.photovault.core.database.entity.LabelEntity
import dev.jskrzypczak.photovault.core.domain.id.LabelId
import dev.jskrzypczak.photovault.core.domain.model.Label
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LabelRepositoryImplTest {

    @Test
    fun `observeAll emits labels from DAO mapped to domain`() = runTest {
        val dao = FakeLabelDao().apply {
            items.value = listOf(LabelEntity(id = "label-orange", name = "orange", colorHex = "#FF8B45"))
        }
        val repo = LabelRepositoryImpl(labelDao = dao, dispatchers = TestAppDispatchers())

        repo.observeAll().test {
            assertEquals(
                listOf(Label(id = LabelId("label-orange"), name = "orange", colorHex = "#FF8B45")),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
