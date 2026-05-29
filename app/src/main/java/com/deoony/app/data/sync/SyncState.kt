package com.deoony.app.data.sync

sealed class SyncState {
    object IDLE : SyncState()
    data class SYNCING(val message: String, val progress: Float = 0.0f) : SyncState()
    data class SUCCESS(val message: String, val backupCode: String = "", val timestamp: Long = System.currentTimeMillis()) : SyncState()
    data class FAILED(val message: String) : SyncState()
    object FIREBASE_NOT_CONFIGURED : SyncState()
    object SIGNED_OUT : SyncState()
    object OFFLINE : SyncState()
}
