package com.deoony.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.deoony.app.data.database.*
import com.deoony.app.data.repository.DebtRepository
import com.deoony.app.data.sync.BackupManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {

    private val database = DebtDatabase.getDatabase(application)
    private val repository = DebtRepository(application, database.debtDao())

    // --- State Flows ---
    val tabs: StateFlow<List<TabEntity>> = repository.allTabsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val persons: StateFlow<List<PersonEntity>> = repository.allPersonsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtsWithDetails: StateFlow<List<DebtWithDetails>> = repository.allDebtsWithDetailsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filter & UI States ---
    private val _selectedTabId = MutableStateFlow<Long?>(null) // null means 'All'
    val selectedTabId: StateFlow<Long?> = _selectedTabId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- UI Toast/Alert Message ---
    private val _uiMessage = MutableStateFlow<String?>(null)
    val uiMessage: StateFlow<String?> = _uiMessage.asStateFlow()

    // Filtered Debts Flow
    val filteredDebts: StateFlow<List<DebtWithDetails>> = combine(
        debtsWithDetails, selectedTabId, searchQuery
    ) { debtsList, tabId, query ->
        debtsList.filter { details ->
            val matchTab = tabId == null || details.debt.tabId == tabId
            val matchName = details.person.name.contains(query, ignoreCase = true)
            val matchNotes = details.debt.notes.contains(query, ignoreCase = true)
            matchTab && (matchName || matchNotes)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial calculations
    val totalToReceive: StateFlow<Double> = debtsWithDetails.map { list ->
        list.filter { !it.debt.isPaid && !it.debt.isCancelled && it.debt.amount > 0 }
            .sumOf { details ->
                val paid = details.payments.sumOf { it.amountPaid }
                details.debt.amount - paid
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalToPay: StateFlow<Double> = debtsWithDetails.map { list ->
        list.filter { !it.debt.isPaid && !it.debt.isCancelled && it.debt.amount < 0 }
            .sumOf { details ->
                val paid = details.payments.sumOf { it.amountPaid }
                Math.abs(details.debt.amount) - paid
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun selectTab(tabId: Long?) {
        _selectedTabId.value = tabId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    // --- Tab Operations ---
    fun addTab(name: String, color: Int, icon: String) {
        viewModelScope.launch {
            repository.insertTab(TabEntity(name = name, color = color, icon = icon))
        }
    }

    fun deleteTab(tab: TabEntity) {
        viewModelScope.launch {
            val deleted = repository.deleteTab(tab)
            if (!deleted) {
                _uiMessage.value = "لا يمكن حذف هذا القسم لأنه يحتوي على ديون."
            } else {
                _uiMessage.value = "تم حذف القسم بنجاح."
            }
        }
    }

    // --- Person Operations ---
    fun addPerson(name: String, phoneNumber: String? = null, email: String? = null, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertPerson(PersonEntity(name = name, phoneNumber = phoneNumber, email = email))
            onComplete(id)
        }
    }

    fun deletePerson(person: PersonEntity) {
        viewModelScope.launch {
            repository.deletePerson(person)
        }
    }

    // --- Debt Operations ---
    fun addDebt(personId: Long, tabId: Long, amount: Double, notes: String, dueDate: Long?) {
        viewModelScope.launch {
            repository.insertDebt(
                DebtEntity(
                    personId = personId,
                    tabId = tabId,
                    amount = amount,
                    notes = notes,
                    dueDate = dueDate
                )
            )
        }
    }

    fun toggleDebtPaid(details: DebtWithDetails) {
        viewModelScope.launch {
            val debt = details.debt
            val isCurrentlyPaid = debt.isPaid
            val updatedDebt = debt.copy(isPaid = !isCurrentlyPaid)
            repository.updateDebt(updatedDebt)
        }
    }

    fun cancelDebt(details: DebtWithDetails) {
        viewModelScope.launch {
            val updatedDebt = details.debt.copy(isCancelled = true)
            repository.updateDebt(updatedDebt)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // --- Payment Operations ---
    fun addPayment(debtId: Long, amount: Double, notes: String) {
        viewModelScope.launch {
            repository.addPayment(
                PaymentEntity(
                    debtId = debtId,
                    amountPaid = amount,
                    notes = notes
                )
            )
        }
    }

    // --- Backup & Restore ---
    fun exportBackupData(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val backupJson = BackupManager.exportBackup(repository)
            onSuccess(backupJson)
        }
    }

    fun importBackupData(json: String, isMerge: Boolean) {
        viewModelScope.launch {
            val result = if (isMerge) {
                BackupManager.importBackupMerge(repository, json)
            } else {
                BackupManager.importBackupReplace(repository, json)
            }
            result.onSuccess {
                _uiMessage.value = "تمت استعادة البيانات بنجاح."
            }.onFailure { exception ->
                _uiMessage.value = exception.message ?: "فشلت عملية استعادة البيانات."
            }
        }
    }

    // --- Sync Message ---
    fun triggerCloudSync() {
        _uiMessage.value = "المزامنة السحابية غير مفعلة حاليًا، يمكنك استخدام النسخ الاحتياطي المحلي."
    }
}
