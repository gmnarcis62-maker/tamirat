package red.line.tamirkar.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager
import red.line.tamirkar.data.RepairTicket

object SmsHelper {

    fun readyMessage(customerName: String, ticket: RepairTicket, shopName: String): String {
        return "سلام $customerName عزیز، دستگاه ${ticket.deviceBrand} ${ticket.deviceModel} شما آماده تحویل است. $shopName"
    }

    /** باز کردن اپ پیامک با متن آماده (نیازی به مجوز خاصی ندارد) */
    fun openSmsApp(activity: Activity, phone: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phone")).apply {
                putExtra("sms_body", message)
            }
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // اپ پیامک روی دستگاه یافت نشد
        }
    }

    /**
     * ارسال خودکار پیامک بدون باز شدن اپ پیامک.
     * نیازمند مجوز android.permission.SEND_SMS (باید پیش از این تابع از کاربر گرفته شود).
     * توجه: ارسال پیامک ممکن است هزینه‌ی اپراتور داشته باشد.
     */
    fun sendAutomatically(phone: String, message: String) {
        val smsManager = SmsManager.getDefault()
        val parts = smsManager.divideMessage(message)
        smsManager.sendMultipartTextMessage(phone, null, parts, null, null)
    }
}
