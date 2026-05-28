package dev.jarkendar.photovault.core.common.di

import dev.jarkendar.photovault.core.common.AppDispatchers
import dev.jarkendar.photovault.core.common.DefaultAppDispatchers
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatchers> { DefaultAppDispatchers() }
}
