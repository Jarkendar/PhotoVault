package dev.jarkendar.photovault.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
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
}