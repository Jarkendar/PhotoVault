package dev.jskrzypczak.photovault.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String,
    val name: String,
    val photoCount: Int = 0,
    val autoEnabled: Boolean = false,
    val rolledOut: Boolean = true,
)