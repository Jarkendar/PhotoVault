package dev.jskrzypczak.photovault.feature.settings.di

import dev.jskrzypczak.photovault.feature.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel { SettingsViewModel(get()) }
}
