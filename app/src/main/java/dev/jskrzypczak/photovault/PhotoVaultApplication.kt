package dev.jskrzypczak.photovault

import android.app.Application
import dev.jarkendar.photovault.core.common.di.commonModule
import dev.jarkendar.photovault.core.data.di.dataModule
import dev.jarkendar.photovault.core.database.di.databaseModule
import dev.jarkendar.photovault.core.network.di.networkModule
import dev.jarkendar.photovault.feature.gallery.di.galleryModule
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
            )
        }
    }
}
