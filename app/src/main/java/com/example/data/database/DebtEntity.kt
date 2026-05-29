package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tabId: Int,
    val title: String,
    val personName: String,
    val amount: Double,
    val isLentByMe: Boolean, // true if "لي" (I lent it), false if "عالي" (I borrowed it)
    val dueDate: String, // "YYYY-MM-DD" style or human readable
    val reminderEnabled: Boolean = false,
    val reminderDateTime: Long? = null,
    val isPaid: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
