package dev.jarkendar.photovault.feature.gallery.di

import dev.jarkendar.photovault.feature.gallery.GalleryViewModel
import dev.jarkendar.photovault.feature.gallery.domain.usecase.ObserveCategoriesUseCase
import dev.jarkendar.photovault.feature.gallery.domain.usecase.ObservePhotosUseCase
import dev.jarkendar.photovault.feature.gallery.domain.usecase.RefreshGalleryUseCase
import dev.jarkendar.photovault.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val galleryModule = module {
    factory { ObservePhotosUseCase(get()) }
    factory { ObserveCategoriesUseCase(get()) }
    factory { RefreshGalleryUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    viewModel { GalleryViewModel(get(), get(), get(), get(), get()) }
}
