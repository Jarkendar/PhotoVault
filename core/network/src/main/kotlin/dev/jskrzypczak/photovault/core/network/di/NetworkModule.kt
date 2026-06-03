package dev.jskrzypczak.photovault.core.network.di

import dev.jskrzypczak.photovault.core.network.BuildConfig
import dev.jskrzypczak.photovault.core.network.BaseUrlProvider
import dev.jskrzypczak.photovault.core.network.api.AuthApi
import dev.jskrzypczak.photovault.core.network.api.CategoriesApi
import dev.jskrzypczak.photovault.core.network.api.HealthApi
import dev.jskrzypczak.photovault.core.network.api.KtorAuthApi
import dev.jskrzypczak.photovault.core.network.api.KtorCategoriesApi
import dev.jskrzypczak.photovault.core.network.api.KtorHealthApi
import dev.jskrzypczak.photovault.core.network.api.KtorLabelsApi
import dev.jskrzypczak.photovault.core.network.api.KtorPhotosApi
import dev.jskrzypczak.photovault.core.network.api.KtorTagsApi
import dev.jskrzypczak.photovault.core.network.api.KtorUploadsApi
import dev.jskrzypczak.photovault.core.network.api.LabelsApi
import dev.jskrzypczak.photovault.core.network.api.PhotosApi
import dev.jskrzypczak.photovault.core.network.api.TagsApi
import dev.jskrzypczak.photovault.core.network.api.UploadsApi
import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import dev.jskrzypczak.photovault.core.network.createPhotoVaultHttpClient
import io.ktor.client.HttpClient
import org.koin.dsl.module

val networkModule = module {
    // TokenStore is bound in :core:data's dataModule (EncryptedTokenStore).
    // BaseUrlProvider is also bound there (ServerSettingsRepositoryImpl).
    single<HttpClient> {
        createPhotoVaultHttpClient(
            baseUrlProvider = get<BaseUrlProvider>(),
            tokenStore = get<TokenStore>(),
            enableLogging = BuildConfig.DEBUG,
        )
    }
    single<AuthApi> { KtorAuthApi(get()) }
    single<HealthApi> { KtorHealthApi(get()) }
    single<PhotosApi> { KtorPhotosApi(get()) }
    single<UploadsApi> { KtorUploadsApi(get()) }
    single<TagsApi> { KtorTagsApi(get()) }
    single<CategoriesApi> { KtorCategoriesApi(get()) }
    single<LabelsApi> { KtorLabelsApi(get()) }
}
