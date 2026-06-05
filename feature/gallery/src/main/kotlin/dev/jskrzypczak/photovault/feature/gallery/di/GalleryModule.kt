package dev.jskrzypczak.photovault.feature.gallery.di

import dev.jskrzypczak.photovault.feature.gallery.GalleryViewModel
import dev.jskrzypczak.photovault.feature.gallery.PhotoDetailViewModel
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObserveCategoriesUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObservePhotoUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ObservePhotosUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.RefreshGalleryUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleCategoryAutoEnabledUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleFavoriteUseCase
import dev.jskrzypczak.photovault.feature.gallery.domain.usecase.ToggleTagAutoEnabledUseCase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val galleryModule = module {
    factory { ObservePhotosUseCase(get()) }
    factory { ObservePhotoUseCase(get()) }
    factory { ObserveCategoriesUseCase(get()) }
    factory { RefreshGalleryUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
    factory { ToggleTagAutoEnabledUseCase(get()) }
    factory { ToggleCategoryAutoEnabledUseCase(get()) }
    viewModel { GalleryViewModel(get(), get(), get(), get(), get()) }
    viewModel { PhotoDetailViewModel(get(), get(), get(), get(), get(), get()) }
}
