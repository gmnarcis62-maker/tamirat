package red.line.tamirkar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TroubleshootingCategory(val label: String) {
    POWER_ON_FAILURE("روشن نشدن / خاموشی کامل دستگاه"),
    DISPLAY("صفحه نمایش"),
    BATTERY_CHARGING("باتری و شارژ"),
    SOFTWARE("نرم‌افزار و سیستم‌عامل"),
    CAMERA("دوربین"),
    AUDIO("صدا و میکروفون"),
    NETWORK_SIM("شبکه، آنتن و سیم‌کارت"),
    SENSORS("سنسورها و دکمه‌ها"),
    BIOMETRIC("اثر انگشت و تشخیص چهره"),
    CONNECTIVITY("بلوتوث، جی‌پی‌اس و NFC"),
    OVERHEATING("گرم شدن دستگاه"),
    WATER_DAMAGE("آب‌خوردگی"),
    CHARGING_PORT("پورت شارژ و کانکتور")
}

@Entity(tableName = "troubleshooting_guides")
data class TroubleshootingGuide(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: TroubleshootingCategory,
    val brand: String,           // "همه برندها" یا نام برند خاص مثل Samsung / Xiaomi / Apple
    val model: String = "همه مدل‌ها", // مدل خاص مثل "iPhone 7" یا "Galaxy A52" — برای فیلتر دقیق‌تر
    val title: String,           // عنوان کوتاه ایراد
    val symptoms: String,        // علائم قابل مشاهده
    val possibleCauses: String,  // علت‌های احتمالی
    val solutionSteps: String,   // مراحل پیشنهادی رفع ایراد (خط به خط با \n)
    val difficulty: String       // سطح دشواری: "آسان" / "متوسط" / "تخصصی"
)
