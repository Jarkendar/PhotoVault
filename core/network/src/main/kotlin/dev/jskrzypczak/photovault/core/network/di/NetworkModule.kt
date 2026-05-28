package dev.jskrzypczak.photovault.core.network.di

import dev.jskrzypczak.photovault.core.network.api.KtorPhotosApi
import dev.jskrzypczak.photovault.core.network.api.PhotosApi
import dev.jskrzypczak.photovault.core.network.auth.AuthTokenProvider
import dev.jskrzypczak.photovault.core.network.auth.StubAuthTokenProvider
import dev.jskrzypczak.photovault.core.network.createPhotoVaultHttpClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

private const val BASE_URL = "http://10.0.2.2:8080/v1/"

val networkModule = module {
    single<AuthTokenProvider> { StubAuthTokenProvider() }
    single<HttpClient> {
        createPhotoVaultHttpClient(
            baseUrl = BASE_URL,
            tokenProvider = get(),
            enableLogging = true,
        )
    }
    single<PhotosApi> { KtorPhotosApi(get()) }
}
