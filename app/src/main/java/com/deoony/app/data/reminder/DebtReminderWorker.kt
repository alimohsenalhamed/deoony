package com.deoony.app.data.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.deoony.app.MainActivity
import com.deoony.app.data.database.DebtDatabase
import com.deoony.app.R

class DebtReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val debtId = inputData.getLong("DEBT_ID", -1L)
        if (debtId == -1L) return Result.failure()

        Log.d("DebtReminderWorker", "Starting reminder work for debt: $debtId")
        val database = DebtDatabase.getDatabase(context)
        val debtWithDetails = database.debtDao().getDebtWithDetailsById(debtId)

        if (debtWithDetails == null) {
            Log.d("DebtReminderWorker", "Debt $debtId not found in DB.")
            return Result.success()
        }

        val debt = debtWithDetails.debt
        if (debt.isPaid || debt.isCancelled) {
            Log.d("DebtReminderWorker", "No notification shown because debt is already paid or cancelled.")
            return Result.success() // Or ignore
        }

        val personName = debtWithDetails.person.name
        val amount = debt.amount
        val notes = debt.notes

        // Build elegant Notification Channel
        val channelId = "debt_reminders"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "تذكير الديون"
            val descriptionText = "قناة مخصصة لإرسال تذكيرات بالديون المستحقة والمجدولة"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            debtId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val amountText = if (amount > 0) "مستحق لك: ${amount} د.أ" else "مستحق عليك: ${Math.abs(amount)} د.أ"
        val notesText = if (notes.isNotEmpty()) "-\nملاحظة: $notes" else ""
        
        val notificationTitle = "تذكير بسداد دين"
        val notificationContent = "حان موعد سداد الدين الخاص بـ ${personName}.\n$amountText$notesText"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Safe fallback icon
            .setContentTitle(notificationTitle)
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(debtId.toInt(), notification)
        } catch (e: SecurityException) {
            Log.e("DebtReminderWorker", "Permission POST_NOTIFICATIONS is not granted yet", e)
        }

        return Result.success()
    }
}
