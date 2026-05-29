package com.deoony.app.data.repository

import com.deoony.app.data.database.DebtDao
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.DebtPaymentDao
import com.deoony.app.data.database.DebtPaymentEntity
import com.deoony.app.data.database.TabEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class DebtRepository(
    private val debtDao: DebtDao,
    private val debtPaymentDao: DebtPaymentDao
) {

    val allTabs: Flow<List<TabEntity>> = debtDao.getAllTabs()
    val allDebts: Flow<List<DebtEntity>> = debtDao.getAllDebts()

    fun getDebtsForTab(tabId: Int): Flow<List<DebtEntity>> {
        return debtDao.getDebtsByTab(tabId)
    }

    suspend fun insertTab(tab: TabEntity): Long {
        return debtDao.insertTab(tab)
    }

    suspend fun updateTab(tab: TabEntity) {
        debtDao.updateTab(tab)
    }

    suspend fun deleteTab(tab: TabEntity) {
        debtDao.deleteDebtsByTab(tab.id)
        debtDao.deleteTab(tab)
    }

    suspend fun insertDebt(debt: DebtEntity): Long {
        return debtDao.insertDebt(debt)
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
    }

    suspend fun prepopulateIfEmpty() {
        // No default transactions or tabs
    }

    val allPersons = debtDao.getAllPersons()

    suspend fun insertPerson(person: com.deoony.app.data.database.PersonEntity): Long {
        return debtDao.insertPerson(person)
    }

    suspend fun updatePerson(person: com.deoony.app.data.database.PersonEntity) {
        debtDao.updatePerson(person)
    }

    suspend fun deletePerson(person: com.deoony.app.data.database.PersonEntity) {
        debtDao.deletePerson(person)
    }

    fun getPaymentsForDebt(debtId: Int): Flow<List<DebtPaymentEntity>> {
        return debtPaymentDao.getPaymentsForDebt(debtId)
    }

    suspend fun insertPayment(payment: DebtPaymentEntity): Long {
        return debtPaymentDao.insertPayment(payment)
    }

    suspend fun deletePayment(payment: DebtPaymentEntity) {
        debtPaymentDao.deletePayment(payment)
    }
}

