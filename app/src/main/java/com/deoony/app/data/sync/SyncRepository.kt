package com.deoony.app.data.sync

import com.deoony.app.data.database.TabEntity
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.PersonEntity
import com.deoony.app.data.database.DebtPaymentEntity
import com.deoony.app.data.repository.DebtRepository
import kotlinx.coroutines.flow.first

class SyncRepository(private val localRepository: DebtRepository) {

    /**
     * Merge backup items with existing database items safely (conflict resolution).
     * Rule: Avoid duplicates. Check by ID or matching identifier. Newer updatedAtMillis wins.
     */
    suspend fun mergeBackup(
        backupTabs: List<TabEntity>,
        backupDebts: List<DebtEntity>,
        backupPersons: List<PersonEntity>,
        backupPayments: List<DebtPaymentEntity>
    ) {
        // 1. Merge Tabs
        val existingTabs = localRepository.allTabs.first()
        for (tab in backupTabs) {
            val duplicate = existingTabs.find { it.id == tab.id || it.name == tab.name }
            if (duplicate == null) {
                localRepository.insertTab(tab)
            } else {
                // If it exists, let's update it if the backup version is newer or has matching properties
                if (tab.createdAt > duplicate.createdAt) {
                    localRepository.updateTab(tab.copy(id = duplicate.id))
                }
            }
        }

        // 2. Merge Persons
        val existingPersons = localRepository.allPersons.first()
        for (person in backupPersons) {
            val duplicate = existingPersons.find { it.id == person.id || it.name == person.name }
            if (duplicate == null) {
                localRepository.insertPerson(person)
            } else {
                if (person.createdAt > duplicate.createdAt) {
                    localRepository.updatePerson(person.copy(id = duplicate.id))
                }
            }
        }

        // 3. Merge Debts
        // Since debts might have matching titles, person names, and amounts, we can avoid duplicates:
        val existingDebts = localRepository.allDebts.first()
        for (debt in backupDebts) {
            val duplicate = existingDebts.find { 
                it.id == debt.id || 
                (it.title == debt.title && it.personName == debt.personName && it.amountMinor == debt.amountMinor && it.createdAt == debt.createdAt) 
            }
            if (duplicate == null) {
                // Check if the tabId still exists, otherwise link to first tab or insert
                val currentTabs = localRepository.allTabs.first()
                val targetTabId = if (currentTabs.any { it.id == debt.tabId }) {
                    debt.tabId
                } else if (currentTabs.isNotEmpty()) {
                    currentTabs.first().id
                } else {
                    // Create default tab if nothing exists
                    val defaultTabId = localRepository.insertTab(TabEntity(name = "عام", colorHex = "#4A707A"))
                    defaultTabId.toInt()
                }
                
                localRepository.insertDebt(debt.copy(tabId = targetTabId))
            } else {
                // Keep the newer one or update
                if (debt.createdAt >= duplicate.createdAt) {
                    localRepository.updateDebt(debt.copy(id = duplicate.id))
                }
            }
        }

        // 4. Merge Payments
        for (payment in backupPayments) {
            val existingPayments = localRepository.getPaymentsForDebt(payment.debtId).first()
            val duplicate = existingPayments.find { 
                it.id == payment.id || 
                (it.debtId == payment.debtId && it.amountMinor == payment.amountMinor && it.paidAtMillis == payment.paidAtMillis) 
            }
            if (duplicate == null) {
                localRepository.insertPayment(payment)
            }
        }
    }
}
