package com.deoony.app.data.reminder

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import androidx.work.Data
import java.util.concurrent.TimeUnit
import android.util.Log

object DebtReminderScheduler {

    private const val TAG = "DebtReminderScheduler"

    fun scheduleReminder(context: Context, debtId: Int, triggerTimeMillis: Long?) {
        if (triggerTimeMillis == null) {
            cancelReminder(context, debtId)
            return
        }

        val currentTime = System.currentTimeMillis()
        val delayMillis = triggerTimeMillis - currentTime

        // If trigger time is in the past, reschedule to a near future (e.g. 5 seconds) or log.
        val finalDelay = if (delayMillis <= 0) {
            Log.d(TAG, "Trigger time is in the past or now. Setting 5s guard delay.")
            5000L // 5 seconds from now
        } else {
            delayMillis
        }

        val workManager = WorkManager.getInstance(context)
        val data = Data.Builder()
            .putInt("debt_id", debtId)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DebtReminderWorker>()
            .setInputData(data)
            .setInitialDelay(finalDelay, TimeUnit.MILLISECONDS)
            .addTag("reminder_debt_$debtId")
            .build()

        val uniqueWorkName = "debt_reminder_work_$debtId"
        
        workManager.enqueueUniqueWork(
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE, // Replaces any old work with the modified/new trigger time!
            workRequest
        )
        Log.d(TAG, "Scheduled reminder for debt $debtId with delay $finalDelay ms")
    }

    fun cancelReminder(context: Context, debtId: Int) {
        val workManager = WorkManager.getInstance(context)
        val uniqueWorkName = "debt_reminder_work_$debtId"
        workManager.cancelUniqueWork(uniqueWorkName)
        Log.d(TAG, "Cancelled reminder for debt $debtId")
    }
}
