package com.deoony.app.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deoony.app.MainActivity
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.TabEntity
import com.deoony.app.data.repository.DebtRepository
import com.deoony.app.data.sync.SyncState
import androidx.room.withTransaction
import com.deoony.app.data.sync.SyncManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ThemePreference {
    SYSTEM, LIGHT, DARK
}

class DebtViewModel(private val repository: DebtRepository) : ViewModel() {

    // Themes
    private val _themePreference = MutableStateFlow(ThemePreference.LIGHT)
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    private val _backgroundHex = MutableStateFlow<String?>(null)
    val backgroundHex: StateFlow<String?> = _backgroundHex.asStateFlow()

    // Tabs
    val tabs: StateFlow<List<TabEntity>> = repository.allTabs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow<TabEntity?>(null)
    val selectedTab: StateFlow<TabEntity?> = _selectedTab.asStateFlow()

    // Debts
    // Persons
    val persons: StateFlow<List<com.deoony.app.data.database.PersonEntity>> = repository.allPersons
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _debtsForSelectedTab = MutableStateFlow<List<DebtEntity>>(emptyList())
    val debtsForSelectedTab: StateFlow<List<DebtEntity>> = _debtsForSelectedTab.asStateFlow()

    val allDebts: StateFlow<List<DebtEntity>> = repository.allDebts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allPayments: StateFlow<List<com.deoony.app.data.database.DebtPaymentEntity>> = repository.allPayments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Sync
    val syncManager = SyncManager()
    val syncState: StateFlow<SyncState> = syncManager.syncState

    fun updateSyncState(state: SyncState) {
        syncManager.setSyncState(state)
    }

    // Active alert/reminders triggered list (simulated)
    private val _triggeredReminders = MutableStateFlow<List<String>>(emptyList())
    val triggeredReminders = _triggeredReminders.asStateFlow()

    init {
        viewModelScope.launch {
            // First, populate defaults if empty
            repository.prepopulateIfEmpty()
            
            // Collect tabs and auto-select the first one
            tabs.collectLatest { tabList ->
                if (tabList.isNotEmpty() && _selectedTab.value == null) {
                    _selectedTab.value = tabList.first()
                }
                updateDebtsCollection()
            }
        }

        viewModelScope.launch {
            _selectedTab.collectLatest {
                updateDebtsCollection()
            }
        }
    }

    private fun updateDebtsCollection() {
        val currentTab = _selectedTab.value
        if (currentTab != null) {
            viewModelScope.launch {
                repository.getDebtsForTab(currentTab.id).collectLatest { debts ->
                    _debtsForSelectedTab.value = debts
                }
            }
        } else {
            _debtsForSelectedTab.value = emptyList()
        }
    }

    fun selectTab(tab: TabEntity) {
        _selectedTab.value = tab
    }

    fun setTheme(pref: ThemePreference) {
        _themePreference.value = pref
    }

    fun setBackgroundHex(hex: String?) {
        _backgroundHex.value = hex
    }

    // TAB CRUD
    fun addTab(name: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            val newTab = TabEntity(name = name, colorHex = colorHex, iconName = iconName)
            val newlyCreatedId = repository.insertTab(newTab)
            // Automatically select the new tab
            _selectedTab.value = TabEntity(id = newlyCreatedId.toInt(), name = name, colorHex = colorHex, iconName = iconName)
        }
    }

    fun editTab(tab: TabEntity, newName: String, newColorHex: String, newIconName: String) {
        viewModelScope.launch {
            val updated = tab.copy(name = newName, colorHex = newColorHex, iconName = newIconName)
            repository.updateTab(updated)
            if (_selectedTab.value?.id == tab.id) {
                _selectedTab.value = updated
            }
        }
    }

    fun deleteTab(tab: TabEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTab(tab)
                val currentTabs = tabs.value
                val remainingTabs = currentTabs.filter { it.id != tab.id }
                if (remainingTabs.isNotEmpty()) {
                    _selectedTab.value = remainingTabs.first()
                } else {
                    _selectedTab.value = null
                }
            } catch (e: Exception) {
                // Ignore or handle gracefully
            }
        }
    }

    // DEBT CRUD
    fun addDebt(
        title: String,
        personName: String,
        amount: Double,
        isLentByMe: Boolean,
        dueDate: String,
        reminderEnabled: Boolean,
        reminderDateTime: Long?,
        notes: String
    ) {
        val currentTab = _selectedTab.value ?: return
        viewModelScope.launch {
            val calculatedCurrency = when {
                title.contains("(ر.س)") -> "SAR"
                title.contains("(ر.ي)") -> "YER"
                title.contains("($)") -> "USD"
                else -> "YER"
            }
            val calculatedDueDateMillis = com.deoony.app.ui.util.DateUtils.parseStringToMillis(dueDate)

            val debt = DebtEntity(
                tabId = currentTab.id,
                title = title,
                personName = personName,
                amountMinor = Math.round(amount * 100),
                currencyCode = calculatedCurrency,
                currencyScale = 2,
                isLentByMe = isLentByMe,
                dueDate = dueDate,
                dueDateMillis = calculatedDueDateMillis,
                reminderEnabled = reminderEnabled,
                reminderDateTime = reminderDateTime,
                isPaid = false,
                status = "ACTIVE",
                notes = notes
            )
            repository.insertDebt(debt)
        }
    }

    fun editDebt(
        debt: DebtEntity,
        title: String,
        personName: String,
        amount: Double,
        isLentByMe: Boolean,
        dueDate: String,
        reminderEnabled: Boolean,
        reminderDateTime: Long?,
        notes: String
    ) {
        viewModelScope.launch {
            val calculatedCurrency = when {
                title.contains("(ر.س)") -> "SAR"
                title.contains("(ر.ي)") -> "YER"
                title.contains("($)") -> "USD"
                else -> debt.currencyCode
            }
            val calculatedDueDateMillis = com.deoony.app.ui.util.DateUtils.parseStringToMillis(dueDate)
            val updatedStatus = if (debt.isPaid) "PAID" else "ACTIVE"

            val updated = debt.copy(
                title = title,
                personName = personName,
                amountMinor = Math.round(amount * 100),
                currencyCode = calculatedCurrency,
                currencyScale = 2,
                isLentByMe = isLentByMe,
                dueDate = dueDate,
                dueDateMillis = calculatedDueDateMillis,
                reminderEnabled = reminderEnabled,
                reminderDateTime = reminderDateTime,
                status = updatedStatus,
                notes = notes
            )
            repository.updateDebt(updated)
        }
    }

    fun toggleDebtPaid(debt: DebtEntity) {
        viewModelScope.launch {
            val newIsPaid = !debt.isPaid
            val updatedStatus = if (newIsPaid) "PAID" else "ACTIVE"
            val updated = debt.copy(
                isPaid = newIsPaid,
                status = updatedStatus
            )
            repository.updateDebt(updated)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch {
            repository.deleteDebt(debt)
        }
    }

    // Person CRUD
    fun addPerson(name: String, defaultType: String, iconName: String) {
        viewModelScope.launch {
            val newPerson = com.deoony.app.data.database.PersonEntity(
                name = name,
                defaultType = defaultType,
                iconName = iconName
            )
            repository.insertPerson(newPerson)
        }
    }

    fun updatePerson(person: com.deoony.app.data.database.PersonEntity) {
        viewModelScope.launch {
            repository.updatePerson(person)
        }
    }

    fun deletePerson(person: com.deoony.app.data.database.PersonEntity) {
        viewModelScope.launch {
            repository.deletePerson(person)
        }
    }

    // Simulated Alarm/Reminder Trigger helper
    fun simulateNotificationTrigger(debtTitle: String, person: String, isLent: Boolean, amount: Double) {
        viewModelScope.launch {
            val directionText = if (isLent) "المستحقة لك من" else "المطلوب سدادها إلى"
            val message = "تنبيه سداد: موعد سداد دين ($debtTitle) بقيمة $amount ر.س $directionText ($person) حان الآن!"
            val currentList = _triggeredReminders.value.toMutableList()
            currentList.add(0, message)
            _triggeredReminders.value = currentList
        }
    }

    fun clearTriggeredReminder(index: Int) {
        val currentList = _triggeredReminders.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _triggeredReminders.value = currentList
        }
    }

    fun clearAllTriggeredReminders() {
        _triggeredReminders.value = emptyList()
    }

    // BACKUP & CLOUD SYNC UTILITIES
    fun triggerCloudSync() {
        viewModelScope.launch {
            syncManager.performSync(
                tabs = tabs.value,
                debts = allDebts.value,
                persons = persons.value,
                payments = allPayments.value
            )
        }
    }

    fun dismissSyncState() {
        syncManager.resetState()
    }

    // Convert all tabs, persons, debts, and processes to complete backup JSON format
    fun getLocalBackupJson(): String {
        return generateBackupJson(
            tabsList = tabs.value,
            debtsList = allDebts.value,
            personsList = persons.value,
            paymentsList = allPayments.value
        )
    }

    private fun generateBackupJson(
        tabsList: List<TabEntity>,
        debtsList: List<DebtEntity>,
        personsList: List<com.deoony.app.data.database.PersonEntity>,
        paymentsList: List<com.deoony.app.data.database.DebtPaymentEntity>
    ): String {
        val root = JSONObject()
        
        // Metadata
        val metadata = JSONObject()
        metadata.put("backupVersion", 2)
        metadata.put("appVersion", "1.0")
        metadata.put("databaseVersion", 4)
        metadata.put("exportedAtMillis", System.currentTimeMillis())
        metadata.put("generator", "Deyoni_Secure_Backend_v2")
        root.put("metadata", metadata)

        // Part 1: Tabs
        val tabsArray = JSONArray()
        for (tab in tabsList) {
            val tObj = JSONObject()
            tObj.put("id", tab.id)
            tObj.put("name", tab.name)
            tObj.put("colorHex", tab.colorHex)
            tObj.put("iconName", tab.iconName)
            tObj.put("createdAt", tab.createdAt)
            tabsArray.put(tObj)
        }
        root.put("tabs", tabsArray)
        
        // Part 2: Debts
        val debtsArray = JSONArray()
        for (debt in debtsList) {
            val dObj = JSONObject()
            dObj.put("id", debt.id)
            dObj.put("tabId", debt.tabId)
            dObj.put("title", debt.title)
            dObj.put("personName", debt.personName)
            dObj.put("amountMinor", debt.amountMinor)
            dObj.put("currencyCode", debt.currencyCode)
            dObj.put("currencyScale", debt.currencyScale)
            dObj.put("isLentByMe", debt.isLentByMe)
            dObj.put("dueDate", debt.dueDate)
            dObj.put("dueDateMillis", debt.dueDateMillis ?: -1L)
            dObj.put("reminderEnabled", debt.reminderEnabled)
            dObj.put("reminderDateTime", debt.reminderDateTime ?: -1L)
            dObj.put("isPaid", debt.isPaid)
            dObj.put("status", debt.status)
            dObj.put("notes", debt.notes)
            dObj.put("createdAt", debt.createdAt)
            debtsArray.put(dObj)
        }
        root.put("debts", debtsArray)

        // Part 3: Persons
        val personsArray = JSONArray()
        for (person in personsList) {
            val pObj = JSONObject()
            pObj.put("id", person.id)
            pObj.put("name", person.name)
            pObj.put("defaultType", person.defaultType)
            pObj.put("iconName", person.iconName)
            pObj.put("createdAt", person.createdAt)
            personsArray.put(pObj)
        }
        root.put("persons", personsArray)

        // Part 4: Payments
        val paymentsArray = JSONArray()
        for (payment in paymentsList) {
            val payObj = JSONObject()
            payObj.put("id", payment.id)
            payObj.put("debtId", payment.debtId)
            payObj.put("amountMinor", payment.amountMinor)
            payObj.put("currencyCode", payment.currencyCode)
            payObj.put("paidAtMillis", payment.paidAtMillis)
            payObj.put("note", payment.note)
            payObj.put("createdAtMillis", payment.createdAtMillis)
            paymentsArray.put(payObj)
        }
        root.put("payments", paymentsArray)
        
        return root.toString(4)
    }

    // Restore Database from a backup string safely (supporting either Replace or Merge mode)
    fun restoreFromBackupText(
        context: Context,
        backupText: String,
        isMerge: Boolean,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val cleanText = backupText.trim()
                if (cleanText.isEmpty()) {
                    onComplete(false, "النص البرمجي للنسخة الاحتياطية فارغ!")
                    return@launch
                }
                
                val root = JSONObject(cleanText)
                
                // Validate metadata & backupVersion
                val backupVersion: Int
                if (root.has("metadata")) {
                    val metadataObj = root.getJSONObject("metadata")
                    backupVersion = metadataObj.optInt("backupVersion", -1)
                } else if (root.has("version")) {
                    backupVersion = root.getInt("version")
                } else {
                    onComplete(false, "صيغة الرمز الاحتياطي غير صالحة، لم يتم العثور على معلومات الإصدار (metadata).")
                    return@launch
                }

                if (backupVersion != 1 && backupVersion != 2) {
                    onComplete(false, "نسخة الرمز الاحتياطي غير مدعومة! نظام الاستعادة يدعم فقط الإصدارين 1 و 2.")
                    return@launch
                }
                
                // Validate JSON has correct arrays
                if (!root.has("tabs") || !root.has("debts")) {
                    onComplete(false, "صيغة البيانات غير صحيحة، تأكد من إدخال النص السحابي الصحيح للنسخة الاحتياطية.")
                    return@launch
                }

                // 1. Parse Tabs
                val tabsArray = root.getJSONArray("tabs")
                val tabsToInsert = mutableListOf<TabEntity>()
                for (i in 0 until tabsArray.length()) {
                    val tObj = tabsArray.getJSONObject(i)
                    tabsToInsert.add(
                        TabEntity(
                            id = tObj.optInt("id", 0),
                            name = tObj.getString("name"),
                            colorHex = tObj.optString("colorHex", "#4A707A"),
                            iconName = tObj.optString("iconName", "Folder"),
                            createdAt = tObj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }

                // 2. Parse Debts
                val debtsArray = root.getJSONArray("debts")
                val debtsToInsert = mutableListOf<DebtEntity>()
                for (i in 0 until debtsArray.length()) {
                    val dObj = debtsArray.getJSONObject(i)
                    val reminderTime = dObj.optLong("reminderDateTime", -1L)
                    
                    val isPaidVal = dObj.optBoolean("isPaid", false)
                    val statusVal = dObj.optString("status", if (isPaidVal) "PAID" else "ACTIVE")
                    
                    val parsedAmountMinor = if (dObj.has("amountMinor")) {
                        dObj.getLong("amountMinor")
                    } else {
                        Math.round(dObj.getDouble("amount") * 100)
                    }
                    val currCode = dObj.optString("currencyCode", "YER")
                    val currScale = dObj.optInt("currencyScale", 2)
                    
                    val rawDueDate = dObj.optString("dueDate", "")
                    val rawDueDateMillis = dObj.optLong("dueDateMillis", -1L)
                    val parsedDueDateMillis = if (rawDueDateMillis != -1L) rawDueDateMillis else com.deoony.app.ui.util.DateUtils.parseStringToMillis(rawDueDate)

                    debtsToInsert.add(
                        DebtEntity(
                            id = dObj.optInt("id", 0),
                            tabId = dObj.getInt("tabId"),
                            title = dObj.getString("title"),
                            personName = dObj.getString("personName"),
                            amountMinor = parsedAmountMinor,
                            currencyCode = currCode,
                            currencyScale = currScale,
                            isLentByMe = dObj.getBoolean("isLentByMe"),
                            dueDate = rawDueDate,
                            dueDateMillis = parsedDueDateMillis,
                            reminderEnabled = dObj.optBoolean("reminderEnabled", false),
                            reminderDateTime = if (reminderTime == -1L) null else reminderTime,
                            isPaid = isPaidVal,
                            status = statusVal,
                            notes = dObj.optString("notes", ""),
                            createdAt = dObj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }

                // 3. Parse Persons (optional for older backups)
                val personsToInsert = mutableListOf<com.deoony.app.data.database.PersonEntity>()
                if (root.has("persons")) {
                    val personsArray = root.getJSONArray("persons")
                    for (i in 0 until personsArray.length()) {
                        val pObj = personsArray.getJSONObject(i)
                        personsToInsert.add(
                            com.deoony.app.data.database.PersonEntity(
                                id = pObj.optInt("id", 0),
                                name = pObj.getString("name"),
                                defaultType = pObj.optString("defaultType", "غير محدد"),
                                iconName = pObj.optString("iconName", "person"),
                                createdAt = pObj.optLong("createdAt", System.currentTimeMillis())
                            )
                        )
                    }
                }

                // 4. Parse Payments (optional for older backups)
                val paymentsToInsert = mutableListOf<com.deoony.app.data.database.DebtPaymentEntity>()
                if (root.has("payments")) {
                    val paymentsArray = root.getJSONArray("payments")
                    for (i in 0 until paymentsArray.length()) {
                        val pObj = paymentsArray.getJSONObject(i)
                        paymentsToInsert.add(
                            com.deoony.app.data.database.DebtPaymentEntity(
                                id = pObj.optInt("id", 0),
                                debtId = pObj.getInt("debtId"),
                                amountMinor = pObj.getLong("amountMinor"),
                                currencyCode = pObj.optString("currencyCode", "YER"),
                                paidAtMillis = pObj.getLong("paidAtMillis"),
                                note = pObj.optString("note", ""),
                                createdAtMillis = pObj.optLong("createdAtMillis", System.currentTimeMillis())
                            )
                        )
                    }
                }

                // Execution of Import
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val db = com.deoony.app.data.database.AppDatabase.getDatabase(context)
                    db.withTransaction {
                        if (isMerge) {
                            // Use SyncRepository tool to merge intelligently
                            val syncRepo = com.deoony.app.data.sync.SyncRepository(repository)
                            syncRepo.mergeBackup(tabsToInsert, debtsToInsert, personsToInsert, paymentsToInsert)
                        } else {
                            // Pure Replace: Clean all tables and insert back in a single transaction
                            repository.clearAllData()
                            
                            // Insert new items
                            for (tab in tabsToInsert) {
                                repository.insertTab(tab)
                            }
                            for (person in personsToInsert) {
                                repository.insertPerson(person)
                            }
                            for (debt in debtsToInsert) {
                                repository.insertDebt(debt)
                            }
                            for (payment in paymentsToInsert) {
                                repository.insertPayment(payment)
                            }
                        }
                    }
                }

                // Refresh state after successful import
                val allTabsList = repository.allTabs.first()
                if (allTabsList.isNotEmpty()) {
                    _selectedTab.value = allTabsList.first()
                } else {
                    _selectedTab.value = null
                }
                updateDebtsCollection()

                val successMsg = if (isMerge) "تم دمج النسخة الاحتياطية بنجاح!" else "تم استبدال البيانات السابقة واستعادة النسخة بالكامل بنجاح!"
                onComplete(true, successMsg)
            } catch (e: Exception) {
                onComplete(false, "فشل استرداد البيانات: تأكد من نسخ النص بدقة. تفاصيل الخطأ: ${e.localizedMessage}")
            }
        }
    }
}

class DebtViewModelFactory(private val repository: DebtRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DebtViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DebtViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
