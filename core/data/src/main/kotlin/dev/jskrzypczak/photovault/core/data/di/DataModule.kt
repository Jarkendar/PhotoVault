package dev.jskrzypczak.photovault.core.data.di

import dev.jskrzypczak.photovault.core.data.repository.AuthRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.CategoryRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.EncryptedTokenStore
import dev.jskrzypczak.photovault.core.data.repository.LabelRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.PhotoRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.ServerSettingsRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.TagRepositoryImpl
import dev.jskrzypczak.photovault.core.data.repository.UploadRepositoryImpl
import dev.jskrzypczak.photovault.core.data.security.CryptoManager
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import dev.jskrzypczak.photovault.core.domain.repository.CategoryRepository
import dev.jskrzypczak.photovault.core.domain.repository.LabelRepository
import dev.jskrzypczak.photovault.core.domain.repository.PhotoRepository
import dev.jskrzypczak.photovault.core.domain.repository.ServerSettingsRepository
import dev.jskrzypczak.photovault.core.domain.repository.TagRepository
import dev.jskrzypczak.photovault.core.domain.repository.UploadRepository
import dev.jskrzypczak.photovault.core.network.BaseUrlProvider
import dev.jskrzypczak.photovault.core.network.auth.TokenStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    // Application-scoped coroutine scope used to keep DataStore flows alive.
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }

    // ----- Token storage (AES-GCM, Android Keystore) -----
    single { CryptoManager() }
    single<EncryptedTokenStore> { EncryptedTokenStore(context = androidContext(), crypto = get()) }
    single<TokenStore> { get<EncryptedTokenStore>() }

    // ----- Auth repository -----
    single<AuthRepository> { AuthRepositoryImpl(authApi = get(), tokenStore = get()) }

    // ----- Server settings (BaseUrlProvider) -----
    // Lambda breaks the circular dep: HttpClient → BaseUrlProvider → ServerSettingsRepositoryImpl → HttpClient.
    single<ServerSettingsRepositoryImpl> {
        val scope = this
        ServerSettingsRepositoryImpl(
            context = androidContext(),
            applicationScope = get(),
            httpClient = { scope.get() },
        )
    }
    single<ServerSettingsRepository> { get<ServerSettingsRepositoryImpl>() }
    single<BaseUrlProvider> { get<ServerSettingsRepositoryImpl>() }

    // ----- Domain repositories -----
    single<PhotoRepository> { PhotoRepositoryImpl(get(), get(), get(), get(), get(), get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
    single<TagRepository> { TagRepositoryImpl(get(), get(), get()) }
    single<LabelRepository> { LabelRepositoryImpl(get(), get(), get()) }
    single<UploadRepository> { UploadRepositoryImpl(get()) }
}
