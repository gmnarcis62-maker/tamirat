package red.line.tamirkar.util

object Validators {

    private val iranMobileRegex = Regex("^09\\d{9}$")

    /** شماره موبایل ایران را اعتبارسنجی می‌کند (فرمت 09xxxxxxxxx) */
    fun isValidPhone(phone: String): Boolean {
        val cleaned = phone.trim()
        return iranMobileRegex.matches(cleaned)
    }

    fun isNonNegativePrice(value: Double): Boolean = value >= 0.0

    fun isRequired(value: String): Boolean = value.trim().isNotEmpty()

    /** پیام خطای مناسب برای شماره تماس نامعتبر */
    const val PHONE_ERROR = "شماره تماس معتبر نیست (باید به فرم ۰۹xxxxxxxxx باشد)"
    const val PRICE_ERROR = "قیمت نمی‌تواند منفی باشد"
    const val REQUIRED_ERROR = "این فیلد نمی‌تواند خالی باشد"
    const val PIN_ERROR = "کد پین باید دقیقاً ۴ رقم باشد"

    fun isValidPin(pin: String): Boolean = Regex("^\\d{4}$").matches(pin)
}
