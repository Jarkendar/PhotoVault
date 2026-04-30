package dev.jarkendar.photovault.core.network.api

import dev.jarkendar.photovault.core.network.dto.category.CategoryCreateRequestDto
import dev.jarkendar.photovault.core.network.dto.category.CategoryDto
import dev.jarkendar.photovault.core.network.dto.category.CategoryListDto
import dev.jarkendar.photovault.core.network.dto.category.CategoryUpdateRequestDto
import dev.jarkendar.photovault.core.network.error.mapToNetworkError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class KtorCategoriesApi(private val client: HttpClient) : CategoriesApi {

    override suspend fun listCategories(): Result<CategoryListDto> = runCatching {
        val response = client.get("categories")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<CategoryListDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun createCategory(request: CategoryCreateRequestDto): Result<CategoryDto> = runCatching {
        val response = client.post("categories") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<CategoryDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun patchCategory(id: String, request: CategoryUpdateRequestDto): Result<CategoryDto> = runCatching {
        val response = client.patch("categories/$id") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        response.body<CategoryDto>()
    }.recoverCatching { throw mapToNetworkError(it) }

    override suspend fun deleteCategory(id: String): Result<Unit> = runCatching {
        val response = client.delete("categories/$id")
        if (!response.status.isSuccess()) throw mapToNetworkError(response)
        Unit
    }.recoverCatching { throw mapToNetworkError(it) }
}
