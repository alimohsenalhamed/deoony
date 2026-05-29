package com.deoony.app.data.repository

import android.content.Context
import com.deoony.app.data.database.*
import com.deoony.app.data.reminder.DebtReminderScheduler
import kotlinx.coroutines.flow.Flow

class DebtRepository(private val context: Context, private val debtDao: DebtDao) {

    // --- Tabs ---
    val allTabsFlow: Flow<List<TabEntity>> = debtDao.getAllTabsFlow()
    
    suspend fun getAllTabs(): List<TabEntity> = debtDao.getAllTabs()
    
    suspend fun insertTab(tab: TabEntity): Long = debtDao.insertTab(tab)
    
    suspend fun deleteTab(tab: TabEntity): Boolean {
        // Prevent deletion if the tab contains debts
        val count = debtDao.getDebtsCountForTab(tab.id)
        return if (count > 0) {
            false
        } else {
            debtDao.deleteTab(tab)
            true
        }
    }

    // --- Persons ---
    val allPersonsFlow: Flow<List<PersonEntity>> = debtDao.getAllPersonsFlow()
    
    suspend fun getAllPersons(): List<PersonEntity> = debtDao.getAllPersons()
    
    suspend fun insertPerson(person: PersonEntity): Long = debtDao.insertPerson(person)
    
    suspend fun deletePerson(person: PersonEntity) = debtDao.deletePerson(person)

    // --- Debts ---
    val allDebtsWithDetailsFlow: Flow<List<DebtWithDetails>> = debtDao.getAllDebtsWithDetailsFlow()
    
    suspend fun getAllDebtsWithDetails(): List<DebtWithDetails> = debtDao.getAllDebtsWithDetails()
    
    suspend fun getDebtWithDetailsById(id: Long): DebtWithDetails? = debtDao.getDebtWithDetailsById(id)

    suspend fun insertDebt(debt: DebtEntity): Long {
        val debtId = debtDao.insertDebt(debt)
        // If a due date is specified AND debt is NOT paid/cancelled, schedule a reminder
        if (debt.dueDate != null && !debt.isPaid && !debt.isCancelled) {
            DebtReminderScheduler.scheduleReminder(context, debtId, debt.dueDate)
        } else {
            DebtReminderScheduler.cancelReminder(context, debtId)
        }
        return debtId
    }

    suspend fun updateDebt(debt: DebtEntity) {
        debtDao.updateDebt(debt)
        // Adjust reminder schedule!
        if (debt.dueDate != null && !debt.isPaid && !debt.isCancelled) {
            DebtReminderScheduler.scheduleReminder(context, debt.id, debt.dueDate)
        } else {
            // Cancel reminder if it is now paid/cancelled or has no due date!
            DebtReminderScheduler.cancelReminder(context, debt.id)
        }
    }

    suspend fun deleteDebt(debt: DebtEntity) {
        debtDao.deleteDebt(debt)
        // Cancel reminder when deleted
        DebtReminderScheduler.cancelReminder(context, debt.id)
    }

    // --- Payments ---
    suspend fun getAllPayments(): List<PaymentEntity> = debtDao.getAllPayments()

    fun getPaymentsForDebtFlow(debtId: Long): Flow<List<PaymentEntity>> = debtDao.getPaymentsForDebtFlow(debtId)

    suspend fun addPayment(payment: PaymentEntity): Long {
        val paymentId = debtDao.insertPayment(payment)
        
        // After recording a payment, check if it pays off the entire debt.
        // We can load details of the debt
        val details = debtDao.getDebtWithDetailsById(payment.debtId)
        if (details != null) {
            val debt = details.debt
            val totalPaid = details.payments.sumOf { it.amountPaid }
            if (totalPaid >= Math.abs(debt.amount)) {
                // Entirely paid! Mark debt as paid and cancel reminder
                val updatedDebt = debt.copy(isPaid = true)
                debtDao.updateDebt(updatedDebt)
                DebtReminderScheduler.cancelReminder(context, debt.id)
            }
        }
        return paymentId
    }

    suspend fun deletePayment(payment: PaymentEntity) {
        debtDao.deletePayment(payment)
        // Un-mark debt as paid if we delete the payment that paid it off
        val details = debtDao.getDebtWithDetailsById(payment.debtId)
        if (details != null) {
            val debt = details.debt
            val totalPaid = details.payments.sumOf { it.amountPaid }
            if (totalPaid < Math.abs(debt.amount) && debt.isPaid) {
                val updatedDebt = debt.copy(isPaid = false)
                debtDao.updateDebt(updatedDebt)
                if (debt.dueDate != null && !debt.isCancelled) {
                    DebtReminderScheduler.scheduleReminder(context, debt.id, debt.dueDate)
                }
            }
        }
    }

    // --- Bulk Restore ---
    suspend fun restoreDatabase(
        tabs: List<TabEntity>,
        persons: List<PersonEntity>,
        debts: List<DebtEntity>,
        payments: List<PaymentEntity>
    ) {
        debtDao.replaceAllData(tabs, persons, debts, payments)
        
        // Re-schedule reminders for all unpaid active debts
        debts.forEach { debt ->
            if (debt.dueDate != null && !debt.isPaid && !debt.isCancelled) {
                DebtReminderScheduler.scheduleReminder(context, debt.id, debt.dueDate)
            } else {
                DebtReminderScheduler.cancelReminder(context, debt.id)
            }
        }
    }
}
