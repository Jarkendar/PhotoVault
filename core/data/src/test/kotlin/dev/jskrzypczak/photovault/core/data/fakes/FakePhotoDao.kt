package dev.jarkendar.photovault.core.data.fakes

import dev.jarkendar.photovault.core.database.dao.PhotoDao
import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.database.entity.LabelEntity
import dev.jarkendar.photovault.core.database.entity.PhotoCategoryCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.PhotoLabelCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoTagCrossRef
import dev.jarkendar.photovault.core.database.entity.TagEntity
import dev.jarkendar.photovault.core.database.relation.PhotoWithRelations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakePhotoDao(
    private val tagSource: () -> List<TagEntity> = { emptyList() },
    private val categorySource: () -> List<CategoryEntity> = { emptyList() },
    private val labelSource: () -> List<LabelEntity> = { emptyList() },
) : PhotoDao {

    val photos = MutableStateFlow<List<PhotoEntity>>(emptyList())
    val photoTags = MutableStateFlow<List<PhotoTagCrossRef>>(emptyList())
    val photoCategories = MutableStateFlow<List<PhotoCategoryCrossRef>>(emptyList())
    val photoLabels = MutableStateFlow<List<PhotoLabelCrossRef>>(emptyList())

    private fun assemble(photo: PhotoEntity): PhotoWithRelations {
        val tags = photoTags.value.filter { it.photoId == photo.id }
            .mapNotNull { ref -> tagSource().firstOrNull { it.id == ref.tagId } }
        val categories = photoCategories.value.filter { it.photoId == photo.id }
            .mapNotNull { ref -> categorySource().firstOrNull { it.id == ref.categoryId } }
        val labels = photoLabels.value.filter { it.photoId == photo.id }
            .mapNotNull { ref -> labelSource().firstOrNull { it.id == ref.labelId } }
        return PhotoWithRelations(photo = photo, tags = tags, categories = categories, labels = labels)
    }

    override fun observeAllWithRelations(): Flow<List<PhotoWithRelations>> =
        photos.map { list -> list.sortedByDescending { it.uploadedAt }.map(::assemble) }

    override fun observeByIdWithRelations(id: String): Flow<PhotoWithRelations?> =
        photos.map { list -> list.firstOrNull { it.id == id }?.let(::assemble) }

    override suspend fun upsertPhotos(photos: List<PhotoEntity>) {
        val current = this.photos.value.associateBy { it.id }.toMutableMap()
        photos.forEach { current[it.id] = it }
        this.photos.value = current.values.toList()
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        photos.value = photos.value.map { if (it.id == id) it.copy(isFavorite = favorite) else it }
    }

    override suspend fun deletePhoto(id: String) {
        photos.value = photos.value.filterNot { it.id == id }
        photoTags.value = photoTags.value.filterNot { it.photoId == id }
        photoCategories.value = photoCategories.value.filterNot { it.photoId == id }
        photoLabels.value = photoLabels.value.filterNot { it.photoId == id }
    }

    override suspend fun deletePhotoTags(photoId: String) {
        photoTags.value = photoTags.value.filterNot { it.photoId == photoId }
    }

    override suspend fun insertPhotoTags(refs: List<PhotoTagCrossRef>) {
        photoTags.value = photoTags.value + refs
    }

    override suspend fun replacePhotoTags(photoId: String, tagIds: List<String>) {
        deletePhotoTags(photoId)
        if (tagIds.isNotEmpty()) {
            insertPhotoTags(tagIds.map { PhotoTagCrossRef(photoId = photoId, tagId = it) })
        }
    }

    override suspend fun deletePhotoCategories(photoId: String) {
        photoCategories.value = photoCategories.value.filterNot { it.photoId == photoId }
    }

    override suspend fun insertPhotoCategories(refs: List<PhotoCategoryCrossRef>) {
        photoCategories.value = photoCategories.value + refs
    }

    override suspend fun replacePhotoCategories(photoId: String, categoryIds: List<String>) {
        deletePhotoCategories(photoId)
        if (categoryIds.isNotEmpty()) {
            insertPhotoCategories(categoryIds.map { PhotoCategoryCrossRef(photoId = photoId, categoryId = it) })
        }
    }

    override suspend fun deletePhotoLabels(photoId: String) {
        photoLabels.value = photoLabels.value.filterNot { it.photoId == photoId }
    }

    override suspend fun insertPhotoLabels(refs: List<PhotoLabelCrossRef>) {
        photoLabels.value = photoLabels.value + refs
    }

    override suspend fun replacePhotoLabels(photoId: String, labelIds: List<String>) {
        deletePhotoLabels(photoId)
        if (labelIds.isNotEmpty()) {
            insertPhotoLabels(labelIds.map { PhotoLabelCrossRef(photoId = photoId, labelId = it) })
        }
    }
}