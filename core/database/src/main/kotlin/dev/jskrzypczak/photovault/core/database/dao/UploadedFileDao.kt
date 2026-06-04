package dev.jskrzypczak.photovault.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.jskrzypczak.photovault.core.database.entity.UploadedFileEntity

@Dao
interface UploadedFileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UploadedFileEntity)

    @Query(
        "SELECT EXISTS(SELECT 1 FROM uploaded_files WHERE fileName = :fileName AND sizeBytes = :sizeBytes)",
    )
    suspend fun exists(fileName: String, sizeBytes: Long): Boolean
}
