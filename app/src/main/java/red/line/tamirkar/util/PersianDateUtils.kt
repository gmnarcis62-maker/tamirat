package red.line.tamirkar.util

import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ابزار تبدیل تاریخ میلادی به شمسی (جلالی) و فرمت‌دهی با اعداد فارسی.
 * این کلاس مستقل و بدون وابستگی خارجی است (الگوریتم استاندارد تبدیل جلالی).
 * تمام نمایش تاریخ در برنامه باید از این کلاس استفاده کند تا برنامه کاملاً فارسی/شمسی بماند.
 */
data class JalaliDate(val year: Int, val month: Int, val day: Int)

object PersianDateUtils {

    private val persianMonthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /** تبدیل اعداد لاتین یک رشته به اعداد فارسی */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            if (ch in '0'..'9') sb.append(persianDigits[ch - '0']) else sb.append(ch)
        }
        return sb.toString()
    }

    /** تبدیل تاریخ میلادی (سال، ماه ۱-۱۲، روز) به شمسی */
    fun gregorianToJalali(gYear: Int, gMonth: Int, gDay: Int): JalaliDate {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gy = gYear - 1600
        val gm = gMonth - 1
        val gd = gDay - 1

        var gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400
        for (i in 0 until gm) gDayNo += gDaysInMonth[i]
        if (gm > 1 && ((gYear % 4 == 0 && gYear % 100 != 0) || gYear % 400 == 0)) gDayNo += 1
        gDayNo += gd

        var jDayNo = gDayNo - 79

        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        var i = 0
        var jm = 1
        while (i < 11 && jDayNo >= jDaysInMonth[i]) {
            jDayNo -= jDaysInMonth[i]
            i++
            jm++
        }
        val jd = jDayNo + 1

        return JalaliDate(jy, jm, jd)
    }

    /** تبدیل تاریخ شمسی به میلادی (سال، ماه ۱-۱۲، روز) */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        var jy2 = jy - 979
        val jm2 = jm - 1
        val jd2 = jd - 1

        var jDayNo = 365 * jy2 + (jy2 / 33) * 8 + (jy2 % 33 + 3) / 4
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        for (i in 0 until jm2) jDayNo += jDaysInMonth[i]
        jDayNo += jd2

        var gDayNo = jDayNo + 79

        var gy = 1600 + 400 * (gDayNo / 146097)
        gDayNo %= 146097

        var leap = true
        if (gDayNo >= 36525) {
            gDayNo--
            gy += 100 * (gDayNo / 36524)
            gDayNo %= 36524
            if (gDayNo >= 365) gDayNo++ else leap = false
        }

        gy += 4 * (gDayNo / 1461)
        gDayNo %= 1461

        if (gDayNo >= 366) {
            leap = false
            gDayNo--
            gy += gDayNo / 365
            gDayNo %= 365
        }

        val gDaysInMonth = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gm = 0
        var i = 0
        while (i < 12) {
            val daysInThisMonth = if (i == 1 && ((gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0)) 29 else gDaysInMonth[i]
            if (gDayNo < daysInThisMonth) {
                gm = i + 1
                break
            }
            gDayNo -= daysInThisMonth
            i++
        }
        val gd = gDayNo + 1

        return Triple(gy, gm, gd)
    }

    /** ساخت timestamp میلی‌ثانیه از تاریخ و ساعت شمسی (برای ثبت یادآوری‌ها) */
    fun jalaliToTimestamp(jy: Int, jm: Int, jd: Int, hour: Int = 9, minute: Int = 0): Long {
        val (gy, gm, gd) = jalaliToGregorian(jy, jm, jd)
        val cal = Calendar.getInstance()
        cal.set(gy, gm - 1, gd, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun fromTimestamp(timestampMillis: Long): JalaliDate {
        val cal = Calendar.getInstance()
        cal.time = Date(timestampMillis)
        return gregorianToJalali(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    /** فرمت کوتاه تاریخ: ۱۴۰۳/۰۶/۲۸ */
    fun formatDate(timestampMillis: Long): String {
        val j = fromTimestamp(timestampMillis)
        val raw = "%04d/%02d/%02d".format(j.year, j.month, j.day)
        return toPersianDigits(raw)
    }

    /** فرمت کامل با نام ماه: ۲۸ شهریور ۱۴۰۳ */
    fun formatDateLong(timestampMillis: Long): String {
        val j = fromTimestamp(timestampMillis)
        val monthName = persianMonthNames[(j.month - 1).coerceIn(0, 11)]
        return "${toPersianDigits(j.day.toString())} $monthName ${toPersianDigits(j.year.toString())}"
    }

    /** فرمت تاریخ و ساعت: ۱۴۰۳/۰۶/۲۸ - ۱۴:۳۰ */
    fun formatDateTime(timestampMillis: Long): String {
        val cal = Calendar.getInstance()
        cal.time = Date(timestampMillis)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val time = "%02d:%02d".format(hour, minute)
        return "${formatDate(timestampMillis)} - ${toPersianDigits(time)}"
    }

    /** فقط ساعت: ۱۴:۳۰ */
    fun formatTime(timestampMillis: Long): String {
        val cal = Calendar.getInstance()
        cal.time = Date(timestampMillis)
        val raw = "%02d:%02d".format(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
        return toPersianDigits(raw)
    }

    /** تبدیل یک عدد صحیح معمولی به رشته با اعداد فارسی (برای قیمت، تعداد و ...) */
    fun toPersianNumber(number: Long): String = toPersianDigits(number.toString())
    fun toPersianNumber(number: Int): String = toPersianDigits(number.toString())

    /** فرمت مبلغ با جداکننده هزارگان و اعداد فارسی، به‌همراه واحد تومان: ۱۲۵٬۰۰۰ تومان */
    fun formatToman(amount: Double): String {
        val rounded = Math.round(amount)
        val formatted = String.format(Locale.US, "%,d", rounded).replace(',', '٬')
        return "${toPersianDigits(formatted)} تومان"
    }
}
