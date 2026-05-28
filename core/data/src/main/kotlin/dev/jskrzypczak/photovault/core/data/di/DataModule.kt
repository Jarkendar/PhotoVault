package dev.jarkendar.photovault.core.data.di

import dev.jarkendar.photovault.core.data.repository.CategoryRepositoryImpl
import dev.jarkendar.photovault.core.data.repository.LabelRepositoryImpl
import dev.jarkendar.photovault.core.data.repository.PhotoRepositoryImpl
import dev.jarkendar.photovault.core.data.repository.TagRepositoryImpl
import dev.jarkendar.photovault.core.domain.repository.CategoryRepository
import dev.jarkendar.photovault.core.domain.repository.LabelRepository
import dev.jarkendar.photovault.core.domain.repository.PhotoRepository
import dev.jarkendar.photovault.core.domain.repository.TagRepository
import org.koin.dsl.module

val dataModule = module {
    single<PhotoRepository> { PhotoRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
    single<TagRepository> { TagRepositoryImpl(get(), get()) }
    single<LabelRepository> { LabelRepositoryImpl(get(), get()) }
}
