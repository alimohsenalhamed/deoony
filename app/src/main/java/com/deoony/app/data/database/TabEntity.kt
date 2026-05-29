package com.deoony.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String = "#4A707A", // Default elegant calm teal/blue-grey
    val iconName: String = "Folder", // Default icon
    val createdAt: Long = System.currentTimeMillis()
)
