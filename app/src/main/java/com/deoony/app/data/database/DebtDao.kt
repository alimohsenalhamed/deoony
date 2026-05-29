package com.deoony.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    // --- Tabs ---
    @Query("SELECT * FROM tabs ORDER BY name ASC")
    fun getAllTabsFlow(): Flow<List<TabEntity>>

    @Query("SELECT * FROM tabs ORDER BY name ASC")
    suspend fun getAllTabs(): List<TabEntity>

    @Query("SELECT * FROM tabs WHERE id = :id")
    suspend fun getTabById(id: Long): TabEntity?

    @Query("SELECT COUNT(*) FROM debts WHERE tabId = :tabId AND isPaid = 0 AND isCancelled = 0")
    suspend fun getUnpaidDebtsCountForTab(tabId: Long): Int

    @Query("SELECT COUNT(*) FROM debts WHERE tabId = :tabId")
    suspend fun getDebtsCountForTab(tabId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(tab: TabEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTabs(tabs: List<TabEntity>)

    @Delete
    suspend fun deleteTab(tab: TabEntity)

    // --- Persons ---
    @Query("SELECT * FROM persons ORDER BY name ASC")
    fun getAllPersonsFlow(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM persons ORDER BY name ASC")
    suspend fun getAllPersons(): List<PersonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersons(persons: List<PersonEntity>)

    @Delete
    suspend fun deletePerson(person: PersonEntity)

    // --- Debts ---
    @Transaction
    @Query("SELECT * FROM debts ORDER BY createdAt DESC")
    fun getAllDebtsWithDetailsFlow(): Flow<List<DebtWithDetails>>

    @Transaction
    @Query("SELECT * FROM debts ORDER BY createdAt DESC")
    suspend fun getAllDebtsWithDetails(): List<DebtWithDetails>

    @Transaction
    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtWithDetailsById(id: Long): DebtWithDetails?

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getDebtById(id: Long): DebtEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebts(debts: List<DebtEntity>)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    // --- Payments ---
    @Query("SELECT * FROM payments WHERE debtId = :debtId ORDER BY paymentDate DESC")
    fun getPaymentsForDebtFlow(debtId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY paymentDate DESC")
    suspend fun getAllPayments(): List<PaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    // --- Restore Transaction Helpers ---
    @Query("DELETE FROM payments")
    suspend fun clearAllPayments()

    @Query("DELETE FROM debts")
    suspend fun clearAllDebts()

    @Query("DELETE FROM persons")
    suspend fun clearAllPersons()

    @Query("DELETE FROM tabs")
    suspend fun clearAllTabs()

    @Transaction
    suspend fun replaceAllData(
        tabs: List<TabEntity>,
        persons: List<PersonEntity>,
        debts: List<DebtEntity>,
        payments: List<PaymentEntity>
    ) {
        clearAllPayments()
        clearAllDebts()
        clearAllPersons()
        clearAllTabs()

        insertTabs(tabs)
        insertPersons(persons)
        insertDebts(debts)
        insertPayments(payments)
    }
}
