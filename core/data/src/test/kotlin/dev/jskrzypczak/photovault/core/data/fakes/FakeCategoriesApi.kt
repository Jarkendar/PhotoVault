package dev.jskrzypczak.photovault.core.data.fakes

import dev.jskrzypczak.photovault.core.network.api.CategoriesApi
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryCreateRequestDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryListDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryUpdateRequestDto

class FakeCategoriesApi(
    initialList: Result<CategoryListDto> = Result.success(CategoryListDto(items = emptyList())),
) : CategoriesApi {
    var nextListResponse: Result<CategoryListDto> = initialList

    override suspend fun listCategories(): Result<CategoryListDto> = nextListResponse

    override suspend fun createCategory(request: CategoryCreateRequestDto): Result<CategoryDto> =
        error("FakeCategoriesApi.createCategory not expected")

    override suspend fun patchCategory(id: String, request: CategoryUpdateRequestDto): Result<CategoryDto> =
        error("FakeCategoriesApi.patchCategory not expected")

    override suspend fun deleteCategory(id: String): Result<Unit> =
        error("FakeCategoriesApi.deleteCategory not expected")
}
