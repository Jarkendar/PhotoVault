package dev.jarkendar.photovault.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jarkendar.photovault.core.database.converter.InstantConverter
import dev.jarkendar.photovault.core.database.dao.CategoryDao
import dev.jarkendar.photovault.core.database.dao.LabelDao
import dev.jarkendar.photovault.core.database.dao.PhotoDao
import dev.jarkendar.photovault.core.database.dao.TagDao
import dev.jarkendar.photovault.core.database.entity.CategoryEntity
import dev.jarkendar.photovault.core.database.entity.LabelEntity
import dev.jarkendar.photovault.core.database.entity.PhotoCategoryCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoEntity
import dev.jarkendar.photovault.core.database.entity.PhotoLabelCrossRef
import dev.jarkendar.photovault.core.database.entity.PhotoTagCrossRef
import dev.jarkendar.photovault.core.database.entity.TagEntity

@Database(
    entities = [
        PhotoEntity::class,
        TagEntity::class,
        CategoryEntity::class,
        LabelEntity::class,
        PhotoTagCrossRef::class,
        PhotoCategoryCrossRef::class,
        PhotoLabelCrossRef::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(InstantConverter::class)
abstract class PhotoVaultDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun tagDao(): TagDao
    abstract fun categoryDao(): CategoryDao
    abstract fun labelDao(): LabelDao
}