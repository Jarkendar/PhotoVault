package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import dev.jarkendar.photovault.core.network.dto.label.LabelName
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorLabelsApiTest {

    private fun api(handler: MockRequestHandler) = KtorLabelsApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = MockEngine(handler),
        ),
    )

    @Test
    fun `listLabels returns LabelListDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"items":[{"id":"label-red","name":"red","colorHex":"#FF0000","photoCount":0}]}""",
                HttpStatusCode.OK,
                headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val result = api.listLabels()
        assertTrue(result.isSuccess)
        assertEquals(LabelName.RED, result.getOrThrow().items[0].name)
    }
}