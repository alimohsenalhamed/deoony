package com.deoony.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debt_payments")
data class DebtPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val debtId: Int,
    val amountMinor: Long,
    val currencyCode: String = "YER",
    val paidAtMillis: Long,
    val note: String = "",
    val createdAtMillis: Long = System.currentTimeMillis()
)
