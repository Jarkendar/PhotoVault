package dev.jarkendar.photovault.core.common

import kotlinx.coroutines.Dispatchers

class DefaultAppDispatchers : AppDispatchers {
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
    override val main = Dispatchers.Main
    override val mainImmediate = Dispatchers.Main.immediate
}
