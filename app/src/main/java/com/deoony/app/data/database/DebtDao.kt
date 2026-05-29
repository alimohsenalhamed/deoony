package com.deoony.app.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    // Tab operations
    @Query("SELECT * FROM tabs ORDER BY createdAt ASC")
    fun getAllTabs(): Flow<List<TabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTab(tab: TabEntity): Long

    @Update
    suspend fun updateTab(tab: TabEntity)

    @Delete
    suspend fun deleteTab(tab: TabEntity)

    // Debt operations
    @Query("SELECT * FROM debts WHERE tabId = :tabId ORDER BY isPaid ASC, createdAt DESC")
    fun getDebtsByTab(tabId: Int): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts ORDER BY createdAt DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE tabId = :tabId")
    suspend fun deleteDebtsByTab(tabId: Int)

    // Person operations
    @Query("SELECT * FROM persons ORDER BY createdAt DESC")
    fun getAllPersons(): Flow<List<PersonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerson(person: PersonEntity): Long

    @Update
    suspend fun updatePerson(person: PersonEntity)

    @Delete
    suspend fun deletePerson(person: PersonEntity)
}
