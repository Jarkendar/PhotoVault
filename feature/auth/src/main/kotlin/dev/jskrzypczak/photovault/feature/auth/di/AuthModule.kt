package dev.jskrzypczak.photovault.feature.auth.di

import dev.jskrzypczak.photovault.feature.auth.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { LoginViewModel(get()) }
}
