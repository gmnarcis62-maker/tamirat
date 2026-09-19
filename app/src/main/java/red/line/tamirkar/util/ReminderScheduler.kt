package red.line.tamirkar.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import red.line.tamirkar.data.Reminder

object ReminderScheduler {

    private fun pendingIntentFor(context: Context, reminder: Reminder): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TITLE, reminder.title)
            putExtra(ReminderReceiver.EXTRA_MESSAGE, reminder.note ?: "")
            putExtra(ReminderReceiver.EXTRA_ID, reminder.id)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(context: Context, reminder: Reminder) {
        if (reminder.dateTimeMillis <= System.currentTimeMillis()) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = pendingIntentFor(context, reminder)
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminder.dateTimeMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // مجوز SCHEDULE_EXACT_ALARM داده نشده (اندروید ۱۲+)؛
            // در این حالت به‌جای آن از alarmManager.set() با دقت کمتر استفاده کنید.
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.dateTimeMillis, pendingIntent)
        }
    }

    fun cancel(context: Context, reminder: Reminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntentFor(context, reminder))
    }
}
