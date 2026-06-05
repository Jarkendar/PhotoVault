package dev.jskrzypczak.photovault.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jskrzypczak.photovault.core.database.converter.InstantConverter
import dev.jskrzypczak.photovault.core.database.dao.CategoryDao
import dev.jskrzypczak.photovault.core.database.dao.LabelDao
import dev.jskrzypczak.photovault.core.database.dao.PhotoDao
import dev.jskrzypczak.photovault.core.database.dao.TagDao
import dev.jskrzypczak.photovault.core.database.dao.UploadedFileDao
import dev.jskrzypczak.photovault.core.database.entity.CategoryEntity
import dev.jskrzypczak.photovault.core.database.entity.LabelEntity
import dev.jskrzypczak.photovault.core.database.entity.PhotoCategoryCrossRef
import dev.jskrzypczak.photovault.core.database.entity.PhotoEntity
import dev.jskrzypczak.photovault.core.database.entity.PhotoLabelCrossRef
import dev.jskrzypczak.photovault.core.database.entity.PhotoTagCrossRef
import dev.jskrzypczak.photovault.core.database.entity.TagEntity
import dev.jskrzypczak.photovault.core.database.entity.UploadedFileEntity

@Database(
    entities = [
        PhotoEntity::class,
        TagEntity::class,
        CategoryEntity::class,
        LabelEntity::class,
        PhotoTagCrossRef::class,
        PhotoCategoryCrossRef::class,
        PhotoLabelCrossRef::class,
        UploadedFileEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class PhotoVaultDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun tagDao(): TagDao
    abstract fun categoryDao(): CategoryDao
    abstract fun labelDao(): LabelDao
    abstract fun uploadedFileDao(): UploadedFileDao
}