package dev.jskrzypczak.photovault.core.network.auth

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StubTokenStoreTest {

    private val store = StubTokenStore()

    @Test
    fun `accessToken returns default fake value`() = runTest {
        assertEquals("fake-token-stub", store.accessToken())
    }

    @Test
    fun `refreshToken returns default fake value`() = runTest {
        assertEquals("fake-refresh-stub", store.refreshToken())
    }

    @Test
    fun `save updates both tokens`() = runTest {
        store.save("new-access", "new-refresh")
        assertEquals("new-access", store.accessToken())
        assertEquals("new-refresh", store.refreshToken())
    }

    @Test
    fun `clear nullifies both tokens`() = runTest {
        store.clear()
        assertNull(store.accessToken())
        assertNull(store.refreshToken())
    }
}
