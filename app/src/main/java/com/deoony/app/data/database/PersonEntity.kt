package com.deoony.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val defaultType: String = "غير محدد", // "غير محدد", "لي", "علي"
    val iconName: String = "person",
    val createdAt: Long = System.currentTimeMillis()
)
