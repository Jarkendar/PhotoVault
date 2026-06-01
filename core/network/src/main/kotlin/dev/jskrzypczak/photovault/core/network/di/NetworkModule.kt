package dev.jskrzypczak.photovault.core.network.di

import dev.jskrzypczak.photovault.core.network.BuildConfig
import dev.jskrzypczak.photovault.core.network.api.KtorPhotosApi
import dev.jskrzypczak.photovault.core.network.api.PhotosApi
import dev.jskrzypczak.photovault.core.network.auth.AuthTokenProvider
import dev.jskrzypczak.photovault.core.network.auth.StubAuthTokenProvider
import dev.jskrzypczak.photovault.core.network.createPhotoVaultHttpClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    single<AuthTokenProvider> { StubAuthTokenProvider() }
    single<HttpClient> {
        createPhotoVaultHttpClient(
            baseUrl = BuildConfig.BASE_URL,
            tokenProvider = get(),
            enableLogging = true,
        )
    }
    single<PhotosApi> { KtorPhotosApi(get()) }
}
