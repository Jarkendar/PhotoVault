package dev.jskrzypczak.photovault.core.database.di

import androidx.room.Room
import dev.jskrzypczak.photovault.core.database.PhotoVaultDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            PhotoVaultDatabase::class.java,
            "photovault.db",
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<PhotoVaultDatabase>().photoDao() }
    single { get<PhotoVaultDatabase>().tagDao() }
    single { get<PhotoVaultDatabase>().categoryDao() }
    single { get<PhotoVaultDatabase>().labelDao() }
    single { get<PhotoVaultDatabase>().uploadedFileDao() }
}
