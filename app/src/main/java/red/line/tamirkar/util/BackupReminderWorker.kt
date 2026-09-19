package red.line.tamirkar.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * یادآوری هفتگی پشتیبان‌گیری.
 *
 * نکته برای تیم توسعه: پیاده‌سازی همگام‌سازی خودکار و واقعی با فضای ابری (مثل Firebase)
 * نیازمند ساخت یک پروژه Firebase و افزودن فایل پیکربندی اختصاصی (google-services.json)
 * توسط خود تیم است که در این محیط توسعه امکان‌پذیر نبود. به‌عنوان جایگزین کاربردی،
 * این Worker هر هفته یک اعلان یادآوری پشتیبان‌گیری دستی به کاربر نمایش می‌دهد.
 */
class BackupReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        NotificationHelper.show(
            applicationContext,
            notificationId = 9001,
            title = "یادآوری پشتیبان‌گیری",
            message = "مدتی است از اطلاعات تعمیرگاه بک‌آپ نگرفته‌اید. از منوی تنظیمات، یک بک‌آپ تازه تهیه کنید."
        )
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "weekly_backup_reminder"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<BackupReminderWorker>(7, TimeUnit.DAYS).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
