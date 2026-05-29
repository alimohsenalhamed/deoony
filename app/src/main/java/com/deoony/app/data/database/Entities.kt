package com.deoony.app.data.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tabs")
data class TabEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: Int,
    val icon: String
)

@Entity(tableName = "persons")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phoneNumber: String? = null,
    val email: String? = null
)

@Entity(
    tableName = "debts",
    foreignKeys = [
        ForeignKey(
            entity = TabEntity::class,
            parentColumns = ["id"],
            childColumns = ["tabId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tabId"), Index("personId")]
)
data class DebtEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val personId: Long,
    val tabId: Long,
    val amount: Double, // positive = receivable (due to me), negative = payable (due to them)
    val notes: String = "",
    val dueDate: Long? = null,
    val isPaid: Boolean = false,
    val isCancelled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = DebtEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("debtId")]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val debtId: Long,
    val amountPaid: Double,
    val paymentDate: Long = System.currentTimeMillis(),
    val notes: String = ""
)
