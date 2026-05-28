package dev.jarkendar.photovault.core.database.di

import androidx.room.Room
import dev.jarkendar.photovault.core.database.PhotoVaultDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            PhotoVaultDatabase::class.java,
            "photovault.db",
        ).build()
    }
    single { get<PhotoVaultDatabase>().photoDao() }
    single { get<PhotoVaultDatabase>().tagDao() }
    single { get<PhotoVaultDatabase>().categoryDao() }
    single { get<PhotoVaultDatabase>().labelDao() }
}
