package dev.jskrzypczak.photovault.core.network.api

import dev.jskrzypczak.photovault.core.network.dto.category.CategoryCreateRequestDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryListDto
import dev.jskrzypczak.photovault.core.network.dto.category.CategoryUpdateRequestDto

interface CategoriesApi {
    suspend fun listCategories(): Result<CategoryListDto>
    suspend fun createCategory(request: CategoryCreateRequestDto): Result<CategoryDto>
    suspend fun patchCategory(id: String, request: CategoryUpdateRequestDto): Result<CategoryDto>
    suspend fun deleteCategory(id: String): Result<Unit>
}