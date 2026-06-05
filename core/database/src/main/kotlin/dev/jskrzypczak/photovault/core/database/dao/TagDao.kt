package dev.jskrzypczak.photovault.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.jskrzypczak.photovault.core.database.entity.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tags")
    fun observeAll(): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(items: List<TagEntity>)

    @Query("DELETE FROM tags WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE tags SET autoEnabled = :enabled WHERE id = :id")
    suspend fun setAutoEnabled(id: String, enabled: Boolean)
}