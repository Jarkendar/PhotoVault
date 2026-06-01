package dev.jskrzypczak.photovault.core.data.di

import dev.jskrzypczak.photovault.core.data.repository.CategoryRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.LabelRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.PhotoRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.TagRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.UploadRepositoryImpl
import dev.jskrzypczak.photovault.core.domain.repository.CategoryRepository
import dev.jskrzypczak.photovault.core.domain.repository.LabelRepository
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository
import dev.jskrzypczak.photovault.core.domain.repository.TagRepository
import dev.jskrzypczak.photovault.core.domain.repository.UploadRepository
import org.koin.dsl.module

val dataModule = module {
    single<PhotoRepository> { PhotoRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get()) }
    single<TagRepository> { TagRepositoryImpl(get(), get()) }
    single<LabelRepository> { LabelRepositoryImpl(get(), get()) }
    single<UploadRepository> { UploadRepositoryImpl(get()) }
}
