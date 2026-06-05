package dev.jskrzypczak.photovault.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String,
    val photoCount: Int = 0,
    val autoEnabled: Boolean = false,
    val rolledOut: Boolean = true,
)