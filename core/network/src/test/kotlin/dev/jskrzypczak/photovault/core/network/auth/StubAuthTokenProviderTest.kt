package dev.jarkendar.photovault.core.network.auth

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StubAuthTokenProviderTest {

    private val provider = StubAuthTokenProvider()

    @Test
    fun `getAccessToken returns fake-token-stub`() = runTest {
        assertEquals("fake-token-stub", provider.getAccessToken())
    }

    @Test
    fun `getRefreshToken returns fake-refresh-stub`() = runTest {
        assertEquals("fake-refresh-stub", provider.getRefreshToken())
    }
}