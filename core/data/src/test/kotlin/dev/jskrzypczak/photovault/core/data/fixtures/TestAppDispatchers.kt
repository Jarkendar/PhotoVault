package dev.jarkendar.photovault.core.data.fixtures

import dev.jarkendar.photovault.core.common.AppDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class TestAppDispatchers : AppDispatchers {
    private val dispatcher = UnconfinedTestDispatcher()
    override val io = dispatcher
    override val default = dispatcher
    override val main = dispatcher
    override val mainImmediate = dispatcher
}