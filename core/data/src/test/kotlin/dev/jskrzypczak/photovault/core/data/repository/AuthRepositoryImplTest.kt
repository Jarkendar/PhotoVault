package dev.jskrzypczak.photovault.core.data.repository

import app.cash.turbine.test
import dev.jskrzypczak.photovault.core.data.fakes.FakeAuthApi
import dev.jskrzypczak.photovault.core.data.fakes.FakeTokenStore
import dev.jskrzypczak.photovault.core.domain.error.DomainError
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.network.error.NetworkError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryImplTest {

    private fun repo(api: FakeAuthApi = FakeAuthApi(), store: FakeTokenStore = FakeTokenStore()) =
        Pair(AuthRepositoryImpl(api, store), store)

    // ----- login -----

    @Test
    fun `login success saves tokens and transitions to Authenticated`() = runTest {
        val (repo, store) = repo()
        repo.authState.test {
            assertEquals(AuthState.Unknown, awaitItem())

            val result = repo.login("jarek", "secret")
            assertTrue(result.isSuccess)
            assertIs<AuthState.Authenticated>(awaitItem())
        }
        assertEquals("test-access", store.accessToken())
        assertEquals("test-refresh", store.refreshToken())
    }

    @Test
    fun `login with invalid credentials returns InvalidCredentials`() = runTest {
        val api = FakeAuthApi().apply {
            loginResult = Result.failure(
                NetworkError.Unauthenticated("https://photovault.local/errors/invalid-credentials"),
            )
        }
        val (repo, _) = repo(api)
        val result = repo.login("bad", "wrong")
        assertTrue(result.isFailure)
        assertIs<DomainError.InvalidCredentials>(result.exceptionOrNull())
    }

    @Test
    fun `login with network error returns NoConnectivity`() = runTest {
        val api = FakeAuthApi().apply {
            loginResult = Result.failure(NetworkError.NoConnectivity)
        }
        val (repo, _) = repo(api)
        val result = repo.login("u", "p")
        assertTrue(result.isFailure)
        assertIs<DomainError.NoConnectivity>(result.exceptionOrNull())
    }

    // ----- logout -----

    @Test
    fun `logout clears tokens and transitions to Unauthenticated`() = runTest {
        val store = FakeTokenStore(access = "some-access", refresh = "some-refresh")
        val (repo, _) = repo(store = store)
        repo.authState.test {
            awaitItem() // Unknown

            repo.logout()
            assertIs<AuthState.Unauthenticated>(awaitItem())
        }
        assertNull(store.accessToken())
        assertNull(store.refreshToken())
    }

    @Test
    fun `logout calls server best-effort and still clears tokens when server fails`() = runTest {
        val api = FakeAuthApi().apply {
            logoutResult = Result.failure(NetworkError.NoConnectivity)
        }
        val store = FakeTokenStore(access = "access", refresh = "refresh")
        val (repo, _) = repo(api, store)
        repo.logout()
        assertNull(store.accessToken())
    }

    // ----- refreshSession -----

    @Test
    fun `refreshSession with no tokens transitions to Unauthenticated without network call`() = runTest {
        val api = FakeAuthApi()
        val store = FakeTokenStore()   // empty
        val (repo, _) = repo(api, store)
        repo.authState.test {
            awaitItem() // Unknown

            repo.refreshSession()
            assertIs<AuthState.Unauthenticated>(awaitItem())
        }
    }

    @Test
    fun `refreshSession with valid token transitions to Authenticated`() = runTest {
        val store = FakeTokenStore(access = "valid-access")
        val (repo, _) = repo(store = store)
        repo.authState.test {
            awaitItem() // Unknown

            repo.refreshSession()
            val state = awaitItem()
            assertIs<AuthState.Authenticated>(state)
            assertEquals("user-1", state.user.id)
        }
    }

    @Test
    fun `refreshSession with expired token clears tokens and transitions to Unauthenticated`() = runTest {
        val api = FakeAuthApi().apply {
            meResult = Result.failure(
                NetworkError.Unauthenticated("https://photovault.local/errors/invalid-token"),
            )
        }
        val store = FakeTokenStore(access = "expired-access", refresh = "old-refresh")
        val (repo, _) = repo(api, store)
        repo.authState.test {
            awaitItem() // Unknown

            repo.refreshSession()
            assertIs<AuthState.Unauthenticated>(awaitItem())
        }
        assertNull(store.accessToken())
    }
}
