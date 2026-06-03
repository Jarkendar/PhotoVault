package dev.jskrzypczak.photovault

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dev.jskrzypczak.photovault.core.common.di.commonModule
import dev.jskrzypczak.photovault.core.data.di.dataModule
import dev.jskrzypczak.photovault.core.database.di.databaseModule
import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import dev.jskrzypczak.photovault.core.network.di.networkModule
import dev.jskrzypczak.photovault.feature.auth.di.authModule
import dev.jskrzypczak.photovault.feature.gallery.di.galleryModule
import dev.jskrzypczak.photovault.feature.search.di.searchModule
import dev.jskrzypczak.photovault.feature.settings.di.settingsModule
import dev.jskrzypczak.photovault.feature.upload.di.uploadModule
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent

class PhotoVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PhotoVaultApplication)
            modules(
                commonModule,
                databaseModule,
                dataModule,       // provides TokenStore, BaseUrlProvider, AuthRepository
                networkModule,    // provides HttpClient, all Ktor API impls (uses TokenStore from dataModule)
                appModule,        // provides AuthGateViewModel
                authModule,
                galleryModule,
                uploadModule,
                searchModule,
                settingsModule,
            )
        }

        // Install an authenticated OkHttp-backed ImageLoader for Coil so that
        // gallery thumbnails can be fetched from the self-hosted server.
        // We resolve the TokenStore lazily from Koin (startKoin is already done above).
        SingletonImageLoader.setSafe { context ->
            val tokenStore: TokenStore = KoinJavaComponent.get(TokenStore::class.java)
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    // runBlocking is acceptable here: OkHttp interceptors run on
                    // background threads and token reads are fast DataStore operations.
                    val token = runBlocking { tokenStore.accessToken() }
                    val request = if (token != null) {
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $token")
                            .build()
                    } else {
                        chain.request()
                    }
                    chain.proceed(request)
                }
                .build()

            ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))
                }
                .crossfade(true)
                .build()
        }
    }
}
