package com.deoony.app.data.sync

import android.content.Context
import android.util.Log
import com.deoony.app.data.database.TabEntity
import com.deoony.app.data.database.PersonEntity
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.PaymentEntity
import com.deoony.app.data.repository.DebtRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

data class BackupMetadata(
    val appId: String,
    val backupVersion: Int,
    val exportTime: Long
)

data class BackupPayload(
    val metadata: BackupMetadata,
    val tabs: List<TabEntity>,
    val persons: List<PersonEntity>,
    val debts: List<DebtEntity>,
    val payments: List<PaymentEntity>
)

object BackupManager {
    private const val TAG = "BackupManager"
    private const val APP_ID = "com.deoony.app"
    private const val BACKUP_VERSION = 1

    private val gson: Gson by lazy {
        GsonBuilder().setPrettyPrinting().create()
    }

    suspend fun exportBackup(repository: DebtRepository): String {
        try {
            val tabs = repository.getAllTabs()
            val persons = repository.getAllPersons()
            val debtsWithDetails = repository.getAllDebtsWithDetails()
            val debts = debtsWithDetails.map { it.debt }
            val payments = repository.getAllPayments()

            val payload = BackupPayload(
                metadata = BackupMetadata(
                    appId = APP_ID,
                    backupVersion = BACKUP_VERSION,
                    exportTime = System.currentTimeMillis()
                ),
                tabs = tabs,
                persons = persons,
                debts = debts,
                payments = payments
            )
            return gson.toJson(payload)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export backup", e)
            throw e
        }
    }

    suspend fun importBackupReplace(repository: DebtRepository, json: String): Result<Unit> {
        return runCatching {
            // 1. Parse JSON safely first
            val payload = gson.fromJson(json, BackupPayload::class.java)
                ?: throw IllegalArgumentException("ملف النسخ الاحتياطي غير صالح أو فارغ")

            // 2. Validate metadata as requested
            validateBackupPayload(payload)

            // 3. Execution in a single transaction
            repository.restoreDatabase(
                tabs = payload.tabs,
                persons = payload.persons,
                debts = payload.debts,
                payments = payload.payments
            )
            Log.d(TAG, "Backup restored successfully under Replace mode.")
        }
    }

    suspend fun importBackupMerge(repository: DebtRepository, json: String): Result<Unit> {
        return runCatching {
            // 1. Parse JSON and validate completely FIRST without making any modifications
            val payload = gson.fromJson(json, BackupPayload::class.java)
                ?: throw IllegalArgumentException("ملف النسخ الاحتياطي غير صالح أو فارغ")

            validateBackupPayload(payload)

            // 2. If valid, proceed with merging. Let's load the current database state
            val currentTabs = repository.getAllTabs()
            val currentPersons = repository.getAllPersons()
            val currentDebtsWithDetails = repository.getAllDebtsWithDetails()
            val currentDebts = currentDebtsWithDetails.map { it.debt }
            val currentPayments = repository.getAllPayments()

            // Merge TabEntity (unique by ID or name)
            val mergedTabs = (currentTabs + payload.tabs).distinctBy { it.name.trim() }
            
            // Merge PersonEntity (unique by name)
            val mergedPersons = (currentPersons + payload.persons).distinctBy { it.name.trim() }

            // Merge debts (distinct by transaction logic, or keep both, using maxOf ID updates)
            // For simplicity and safety, we combine them, resolving conflicting primary keys
            // Map original backup IDs to merged IDs if they conflict, but here we can keep keys unless they clash, 
            // since this is local merge, key collisions might overwrite. 
            // Better to re-insert merged components safely or treat lists as disjoint, keeping IDs.
            val mergedDebts = (currentDebts + payload.debts).distinctBy { it.id }
            val mergedPayments = (currentPayments + payload.payments).distinctBy { it.id }

            // 3. Run safely inside transaction
            repository.restoreDatabase(
                tabs = mergedTabs,
                persons = mergedPersons,
                debts = mergedDebts,
                payments = mergedPayments
            )
            Log.d(TAG, "Backup restored successfully under Merge mode.")
        }
    }

    private fun validateBackupPayload(payload: BackupPayload) {
        if (payload.metadata == null) {
            throw IllegalArgumentException("بيانات الوصفية (Metadata) مفقودة في ملف النسخ الاحتياطي")
        }
        if (payload.metadata.appId != APP_ID) {
            throw IllegalArgumentException("معرّف التطبيق في النسخة الاحتياطية غير متطابق. معرّف التطبيق الأصلي هو: $APP_ID")
        }
        if (payload.metadata.backupVersion != BACKUP_VERSION) {
            throw IllegalArgumentException("نسخة الاحتياط غير مدعومة. المدعوم حالياً هو نسخة: $BACKUP_VERSION")
        }
        if (payload.tabs == null || payload.persons == null || payload.debts == null || payload.payments == null) {
            throw IllegalArgumentException("مكونات قاعدة البيانات مفقودة في هذا الملف")
        }
    }
}
