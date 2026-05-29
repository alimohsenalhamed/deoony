package com.deoony.app.data.database

import androidx.room.Embedded
import androidx.room.Relation

data class DebtWithDetails(
    @Embedded val debt: DebtEntity,
    
    @Relation(
        parentColumn = "personId",
        entityColumn = "id"
    )
    val person: PersonEntity,

    @Relation(
        parentColumn = "tabId",
        entityColumn = "id"
    )
    val tab: TabEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "debtId"
    )
    val payments: List<PaymentEntity> = emptyList()
)
