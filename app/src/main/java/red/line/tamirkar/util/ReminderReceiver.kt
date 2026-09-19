package red.line.tamirkar.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "یادآوری تعمیرگاه"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""
        val id = intent.getLongExtra(EXTRA_ID, 0L).toInt()
        NotificationHelper.show(context, id, title, message)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_ID = "extra_id"
    }
}
