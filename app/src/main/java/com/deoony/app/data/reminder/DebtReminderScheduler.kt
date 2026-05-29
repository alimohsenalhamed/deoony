package com.deoony.app.data.reminder

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

object DebtReminderScheduler {
    private const val TAG = "DebtReminderScheduler"

    fun scheduleReminder(context: Context, debtId: Long, triggerTimeMillis: Long) {
        val currentTime = System.currentTimeMillis()
        if (triggerTimeMillis <= currentTime) {
            Log.d(TAG, "Not scheduling reminder for debt $debtId because trigger time is in the past or equals current time.")
            return
        }

        val initialDelay = triggerTimeMillis - currentTime
        Log.d(TAG, "Scheduling reminder for debt $debtId in $initialDelay ms")

        val workRequest = OneTimeWorkRequestBuilder<DebtReminderWorker>()
            .setInputData(workDataOf("DEBT_ID" to debtId))
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("reminder_$debtId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_$debtId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun cancelReminder(context: Context, debtId: Long) {
        Log.d(TAG, "Canceling reminder for debt $debtId")
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$debtId")
    }
}
