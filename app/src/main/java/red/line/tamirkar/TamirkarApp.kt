package red.line.tamirkar

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import java.util.Locale
import red.line.tamirkar.data.AppDatabase

/**
 * کلاس اصلی برنامه — نقطه‌ی راه‌اندازی دیتابیس و منابع مشترک
 * توسعه‌دهنده: تیم نرم‌افزاری ردلاین سافت البرز
 * مدیریت: مهندس مهدی رضایی
 *
 * برنامه همیشه به زبان فارسی و جهت راست‌چین اجرا می‌شود، صرف‌نظر از زبان سیستم کاربر.
 */
class TamirkarApp : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updatePersianLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        red.line.tamirkar.util.BackupReminderWorker.schedule(this)
    }

    companion object {
        fun updatePersianLocale(context: Context): Context {
            val locale = Locale("fa", "IR")
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return context.createConfigurationContext(config)
        }
    }
}
