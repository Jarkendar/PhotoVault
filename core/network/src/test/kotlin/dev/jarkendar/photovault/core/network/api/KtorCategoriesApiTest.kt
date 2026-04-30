package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.auth.StubAuthTokenProvider
import dev.jarkendar.photovault.core.network.createPhotoVaultHttpClient
import dev.jarkendar.photovault.core.network.dto.category.CategoryCreateRequestDto
import dev.jarkendar.photovault.core.network.dto.category.CategoryUpdateRequestDto
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

class KtorCategoriesApiTest {

    private fun api(handler: MockRequestHandler) = KtorCategoriesApi(
        createPhotoVaultHttpClient(
            baseUrl = "http://test/v1",
            tokenProvider = StubAuthTokenProvider(),
            engine = MockEngine(handler),
        ),
    )

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    @Test
    fun `listCategories returns CategoryListDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"items":[{"id":"cat-1","name":"A","colorHex":"#000000","photoCount":1}]}""",
                HttpStatusCode.OK,
                jsonHeaders,
            )
        }
        val result = api.listCategories()
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().items.size)
    }

    @Test
    fun `createCategory returns 201 with CategoryDto`() = runTest {
        val api = api { _ ->
            respond(
                """{"id":"cat-001","name":"Natura","colorHex":"#FF8B45","photoCount":0}""",
                HttpStatusCode.Created,
                jsonHeaders,
            )
        }
        val result = api.createCategory(CategoryCreateRequestDto("Natura", "#FF8B45"))
        assertTrue(result.isSuccess)
        assertEquals("cat-001", result.getOrThrow().id)
    }

    @Test
    fun `patchCategory returns updated category`() = runTest {
        val api = api { _ ->
            respond(
                """{"id":"cat-001","name":"Renamed","colorHex":"#FF8B45","photoCount":48}""",
                HttpStatusCode.OK,
                jsonHeaders,
            )
        }
        val result = api.patchCategory("cat-001", CategoryUpdateRequestDto(name = "Renamed"))
        assertTrue(result.isSuccess)
        assertEquals("Renamed", result.getOrThrow().name)
    }

    @Test
    fun `deleteCategory returns success on 204`() = runTest {
        val api = api { _ -> respond("", HttpStatusCode.NoContent) }
        assertTrue(api.deleteCategory("cat-001").isSuccess)
    }
}