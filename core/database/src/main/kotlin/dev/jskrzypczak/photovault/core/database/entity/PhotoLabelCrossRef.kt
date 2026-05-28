package dev.jarkendar.photovault.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "photo_labels",
    primaryKeys = ["photoId", "labelId"],
    foreignKeys = [
        ForeignKey(
            entity = PhotoEntity::class,
            parentColumns = ["id"],
            childColumns = ["photoId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = LabelEntity::class,
            parentColumns = ["id"],
            childColumns = ["labelId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("labelId")],
)
data class PhotoLabelCrossRef(
    val photoId: String,
    val labelId: String,
)