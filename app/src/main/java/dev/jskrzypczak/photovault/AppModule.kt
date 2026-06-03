package dev.jskrzypczak.photovault

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AuthGateViewModel(get()) }
}
