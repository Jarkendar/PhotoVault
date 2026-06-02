package dev.jskrzypczak.photovault.feature.search.di

import dev.jskrzypczak.photovault.feature.search.SearchViewModel
import dev.jskrzypczak.photovault.feature.search.domain.usecase.CountPhotosUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveCategoriesUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveLabelsUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.ObserveTagsUseCase
import dev.jskrzypczak.photovault.feature.search.domain.usecase.SearchPhotosUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val searchModule = module {
    factory { SearchPhotosUseCase(get()) }
    factory { CountPhotosUseCase(get()) }
    factory { ObserveTagsUseCase(get()) }
    factory { ObserveCategoriesUseCase(get()) }
    factory { ObserveLabelsUseCase(get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
}
