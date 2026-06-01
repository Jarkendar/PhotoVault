package dev.jskrzypczak.photovault.feature.upload.di

import androidx.work.WorkManager
import dev.jskrzypczak.photovault.feature.upload.UploadViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uploadModule = module {
    viewModel {
        UploadViewModel(
            workManager = WorkManager.getInstance(androidContext()),
            contentResolver = androidContext().contentResolver,
            dispatchers = get(),
        )
    }
}
