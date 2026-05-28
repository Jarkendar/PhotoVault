package dev.jskrzypczak.photovault.core.common.di

import dev.jskrzypczak.photovault.core.common.AppDispatchers
import dev.jskrzypczak.photovault.core.common.DefaultAppDispatchers
import org.koin.dsl.module

val commonModule = module {
    single<AppDispatchers> { DefaultAppDispatchers() }
}
