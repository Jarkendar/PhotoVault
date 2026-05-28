package dev.jskrzypczak.photovault.core.database.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import dev.jskrzypczak.photovault.core.database.entity.CategoryEntity
import dev.jskrzypczak.photovault.core.database.entity.LabelEntity
import dev.jskrzypczak.photovault.core.database.entity.PhotoCategoryCrossRef
import dev.jskrzypczak.photovault.core.database.entity.PhotoEntity
import dev.jskrzypczak.photovault.core.database.entity.PhotoLabelCrossRef
import dev.jskrzypczak.photovault.core.database.entity.PhotoTagCrossRef
import dev.jskrzypczak.photovault.core.database.entity.TagEntity

data class PhotoWithRelations(
    @Embedded val photo: PhotoEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PhotoTagCrossRef::class,
            parentColumn = "photoId",
            entityColumn = "tagId",
        ),
    )
    val tags: List<TagEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PhotoCategoryCrossRef::class,
            parentColumn = "photoId",
            entityColumn = "categoryId",
        ),
    )
    val categories: List<CategoryEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = PhotoLabelCrossRef::class,
            parentColumn = "photoId",
            entityColumn = "labelId",
        ),
    )
    val labels: List<LabelEntity>,
)
