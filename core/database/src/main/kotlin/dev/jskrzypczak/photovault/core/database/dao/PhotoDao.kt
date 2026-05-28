package dev.jarkendar.photovault.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.jarkendar.photovault.core.database.entity.PhotoCategoryCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.PhotoLabelCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoTagCrossRef
import dev.jarkendar.photovault.core.database.relation.PhotoWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Transaction
    @Query("SELECT * FROM photos ORDER BY uploadedAt DESC")
    fun observeAllWithRelations(): Flow<List<PhotoWithRelations>>

    @Transaction
    @Query("SELECT * FROM photos WHERE id = :id")
    fun observeByIdWithRelations(id: String): Flow<PhotoWithRelations?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPhotos(photos: List<PhotoEntity>)

    @Query("UPDATE photos SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: String, favorite: Boolean)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhoto(id: String)

    @Query("DELETE FROM photo_tags WHERE photoId = :photoId")
    suspend fun deletePhotoTags(photoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoTags(refs: List<PhotoTagCrossRef>)

    @Transaction
    suspend fun replacePhotoTags(photoId: String, tagIds: List<String>) {
        deletePhotoTags(photoId)
        if (tagIds.isNotEmpty()) {
            insertPhotoTags(tagIds.map { PhotoTagCrossRef(photoId = photoId, tagId = it) })
        }
    }

    @Query("DELETE FROM photo_categories WHERE photoId = :photoId")
    suspend fun deletePhotoCategories(photoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoCategories(refs: List<PhotoCategoryCrossRef>)

    @Transaction
    suspend fun replacePhotoCategories(photoId: String, categoryIds: List<String>) {
        deletePhotoCategories(photoId)
        if (categoryIds.isNotEmpty()) {
            insertPhotoCategories(categoryIds.map { PhotoCategoryCrossRef(photoId = photoId, categoryId = it) })
        }
    }

    @Query("DELETE FROM photo_labels WHERE photoId = :photoId")
    suspend fun deletePhotoLabels(photoId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotoLabels(refs: List<PhotoLabelCrossRef>)

    @Transaction
    suspend fun replacePhotoLabels(photoId: String, labelIds: List<String>) {
        deletePhotoLabels(photoId)
        if (labelIds.isNotEmpty()) {
            insertPhotoLabels(labelIds.map { PhotoLabelCrossRef(photoId = photoId, labelId = it) })
        }
    }
}