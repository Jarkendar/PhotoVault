package dev.jskrzypczak.photovault.feature.auth

import app.cash.turbine.test
import dev.jskrzypczak.photovault.core.domain.error.DomainError
import dev.jskrzypczak.photovault.core.domain.model.AuthState
import dev.jskrzypczak.photovault.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class FakeAuthRepository(
    private val loginResult: Result<Unit> = Result.success(Unit),
) : AuthRepository {
    override val authState: Flow<AuthState> = MutableStateFlow(AuthState.Unknown)
    override suspend fun login(username: String, password: String): Result<Unit> = loginResult
    override suspend fun logout() = Unit
    override suspend fun refreshSession() = Unit
}

class LoginViewModelTest {

    @Test
    fun `initial state is empty and idle`() = runTest {
        val vm = LoginViewModel(FakeAuthRepository())
        val state = vm.uiState.value
        assertEquals("", state.username)
        assertEquals("", state.password)
        assertFalse(state.isSubmitting)
        assertNull(state.error)
    }

    @Test
    fun `onUsernameChange updates username and clears error`() = runTest {
        val vm = LoginViewModel(FakeAuthRepository())
        vm.uiState.test {
            awaitItem()  // initial

            vm.onUsernameChange("jarek")
            val state = awaitItem()
            assertEquals("jarek", state.username)
            assertNull(state.error)
        }
    }

    @Test
    fun `onLogin submitting transitions isSubmitting then clears on success`() = runTest {
        val vm = LoginViewModel(FakeAuthRepository(loginResult = Result.success(Unit)))
        vm.uiState.test {
            awaitItem()  // initial

            vm.onLogin()
            // isSubmitting = true (brief)
            val submitting = awaitItem()
            assertTrue(submitting.isSubmitting)

            // isSubmitting = false after success
            val done = awaitItem()
            assertFalse(done.isSubmitting)
            assertNull(done.error)
        }
    }

    @Test
    fun `onLogin with invalid credentials sets INVALID_CREDENTIALS error`() = runTest {
        val vm = LoginViewModel(
            FakeAuthRepository(loginResult = Result.failure(DomainError.InvalidCredentials)),
        )
        vm.uiState.test {
            awaitItem()

            vm.onLogin()
            awaitItem()  // isSubmitting = true

            val errorState = awaitItem()
            assertFalse(errorState.isSubmitting)
            assertEquals(LoginError.INVALID_CREDENTIALS, errorState.error)
        }
    }

    @Test
    fun `onLogin with network error sets NETWORK error`() = runTest {
        val vm = LoginViewModel(
            FakeAuthRepository(loginResult = Result.failure(DomainError.NoConnectivity)),
        )
        vm.uiState.test {
            awaitItem()
            vm.onLogin()
            awaitItem()  // isSubmitting = true
            val errorState = awaitItem()
            assertEquals(LoginError.NETWORK, errorState.error)
        }
    }
}
