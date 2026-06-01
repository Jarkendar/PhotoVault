package dev.jskrzypczak.photovault

import android.app.Application
import dev.jskrzypczak.photovault.core.common.di.commonModule
import dev.jskrzypczak.photovault.core.data.di.dataModule
import dev.jskrzypczak.photovault.core.database.di.databaseModule
import dev.jskrzypczak.photovault.core.network.di.networkModule
import dev.jskrzypczak.photovault.feature.gallery.di.galleryModule
import dev.jskrzypczak.photovault.feature.upload.di.uploadModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoVaultApplication)
            modules(
                commonModule,
                databaseModule,
                networkModule,
                dataModule,
                galleryModule,
                uploadModule,
            )
        }
    }
}
