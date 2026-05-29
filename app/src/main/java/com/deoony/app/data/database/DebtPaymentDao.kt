package com.deoony.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtPaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: DebtPaymentEntity): Long

    @Update
    suspend fun updatePayment(payment: DebtPaymentEntity)

    @Delete
    suspend fun deletePayment(payment: DebtPaymentEntity)

    @Query("SELECT * FROM debt_payments WHERE debtId = :debtId ORDER BY paidAtMillis DESC")
    fun getPaymentsForDebt(debtId: Int): Flow<List<DebtPaymentEntity>>

    @Query("SELECT * FROM debt_payments ORDER BY createdAtMillis DESC")
    fun getAllPayments(): Flow<List<DebtPaymentEntity>>

    @Query("DELETE FROM debt_payments WHERE debtId = :debtId")
    suspend fun deletePaymentsByDebt(debtId: Int)

    @Query("DELETE FROM debt_payments")
    suspend fun deleteAllPayments()
}
