package com.deoony.app.data.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deoony.app.data.database.AppDatabase
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.deoony.app.MainActivity
import java.text.NumberFormat
import java.util.Locale

class DebtReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val debtId = inputData.getInt("debt_id", -1)
        if (debtId == -1) return Result.failure()

        val db = AppDatabase.getDatabase(applicationContext)
        val debt = db.debtDao().getDebtById(debtId)

        // If debt is deleted, paid, or cancelled, do not notify.
        if (debt == null || debt.isPaid || debt.status == "PAID" || debt.status == "CANCELLED") {
            return Result.success()
        }

        // Send the notification
        sendNotification(debt.personName, debt.title, debt.amountMinor, debt.currencyCode)

        return Result.success()
    }

    private fun sendNotification(personName: String, title: String, amountMinor: Long, currencyCode: String) {
        val channelId = "debt_reminders"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel if Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "تذكيرات الديون",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات بمواعيد استحقاق الديون"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Open app on click
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formattedAmount = try {
            val amount = amountMinor / 100.0
            "$amount $currencyCode"
        } catch (e: Exception) {
            "${amountMinor / 100.0} $currencyCode"
        }

        val contentText = if (personName.isNotBlank()) {
            "لديك دين مستحق: $personName - $formattedAmount ($title)"
        } else {
            "لديك دين مستحق بقيمة $formattedAmount ($title)"
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("تذكير دين مستحق")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }
}
