package dev.jskrzypczak.photovault

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoVaultApplication)
            modules(appModule)
        }
    }
}
