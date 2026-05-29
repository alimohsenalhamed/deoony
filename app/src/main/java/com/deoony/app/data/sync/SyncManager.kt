package com.deoony.app.data.sync

import com.deoony.app.data.database.TabEntity
import com.deoony.app.data.database.DebtEntity
import com.deoony.app.data.database.PersonEntity
import com.deoony.app.data.database.DebtPaymentEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay

class SyncManager {
    private val _syncState = MutableStateFlow<SyncState>(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun isFirebaseConfigured(): Boolean {
        // Since google-services.json is absent and the google-services plugin is not in the build script,
        // Firebase is not configured for cloud sync.
        return false
    }

    suspend fun performSync(
        tabs: List<TabEntity>,
        debts: List<DebtEntity>,
        persons: List<PersonEntity>,
        payments: List<DebtPaymentEntity>
    ) {
        _syncState.value = SyncState.SYNCING("جاري فحص اتصال المزامنة السحابية...", 0.1f)
        delay(1000)
        
        // Since Firebase/Firestore is not configured with active configuration scripts,
        // we directly yield FIREBASE_NOT_CONFIGURED status.
        _syncState.value = SyncState.FIREBASE_NOT_CONFIGURED
    }

    fun setSyncState(state: SyncState) {
        _syncState.value = state
    }

    fun resetState() {
        _syncState.value = SyncState.IDLE
    }
}
