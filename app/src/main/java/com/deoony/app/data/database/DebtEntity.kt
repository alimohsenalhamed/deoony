package com.deoony.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tabId: Int,
    val title: String,
    val personName: String,
    val amountMinor: Long,
    val currencyCode: String = "YER",
    val currencyScale: Int = 2,
    val isLentByMe: Boolean, // true if "لي" (I lent it), false if "عالي" (I borrowed it)
    val dueDate: String, // "YYYY-MM-DD" style or human readable
    val dueDateMillis: Long? = null,
    val reminderEnabled: Boolean = false,
    val reminderDateTime: Long? = null,
    val isPaid: Boolean = false,
    val status: String = "ACTIVE", // ACTIVE, PAID, OVERDUE, CANCELLED
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    @get:Ignore
    val amount: Double
        get() = amountMinor / Math.max(1.0, Math.pow(10.0, currencyScale.toDouble()))
}
