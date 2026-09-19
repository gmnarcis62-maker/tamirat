package red.line.tamirkar.billing

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.IntentSender
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.vending.billing.IInAppBillingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * مدیریت خرید درون‌برنامه‌ای نسخه VIP از طریق سرویس In-App Billing مایکت.
 *
 * این کلاس از همان اینترفیس استاندارد Google IAB v3 استفاده می‌کند که مایکت هم
 * برای سازگاری پیاده‌سازی کرده است:
 *   - پکیج سرویس: ir.mservices.market
 *   - Action اتصال: ir.mservices.market.InAppBillingService.BIND
 *   - مجوز لازم: ir.mservices.market.BILLING (در Manifest اضافه شده)
 *
 * نکته مهم برای تیم توسعه: پیش از انتشار نهایی، محصول با شناسه زیر باید
 * دقیقاً در پنل توسعه‌دهندگان مایکت (بخش In-App Billing) ثبت شود:
 *   Product ID: red.line.tamirkar_pro
 */
class MyketBillingManager(private val activity: ComponentActivity) {

    companion object {
        const val VIP_PRODUCT_ID = "red.line.tamirkar_pro"
        private const val MARKET_PACKAGE = "ir.mservices.market"
        private const val BIND_ACTION = "ir.mservices.market.InAppBillingService.BIND"
        private const val API_VERSION = 3
        private const val ITEM_TYPE_INAPP = "inapp"
    }

    private val _isVip = MutableStateFlow(false)
    val isVip: StateFlow<Boolean> = _isVip

    private var billingService: IInAppBillingService? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val purchaseLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // خرید با موفقیت انجام شد؛ برای اطمینان کامل وضعیت را دوباره از سرویس استعلام می‌کنیم
            queryPurchaseStatus()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            billingService = IInAppBillingService.Stub.asInterface(service)
            queryPurchaseStatus()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            billingService = null
        }
    }

    init {
        val serviceIntent = Intent(BIND_ACTION).apply { setPackage(MARKET_PACKAGE) }
        try {
            activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            // اپ مایکت روی دستگاه نصب نیست یا سرویس در دسترس نیست
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                try {
                    billingService?.let { activity.unbindService(serviceConnection) }
                } catch (_: Exception) {
                }
            }
        })
    }

    /** بررسی وضعیت خرید فعلی کاربر از طریق سرویس مایکت */
    fun queryPurchaseStatus() {
        val service = billingService ?: return
        scope.launch {
            try {
                val ownedItems: Bundle = service.getPurchases(API_VERSION, activity.packageName, ITEM_TYPE_INAPP, null)
                val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST") ?: arrayListOf()
                val owned = purchaseDataList.any { data ->
                    try {
                        JSONObject(data).optString("productId") == VIP_PRODUCT_ID
                    } catch (e: Exception) {
                        false
                    }
                }
                _isVip.value = owned
            } catch (e: Exception) {
                // خطا در ارتباط با سرویس؛ وضعیت فعلی حفظ می‌شود
            }
        }
    }

    /** شروع فرآیند خرید نسخه VIP */
    fun launchVipPurchase() {
        val service = billingService
        if (service == null) {
            // سرویس مایکت در دسترس نیست (احتمالاً اپ مایکت نصب نیست)
            return
        }
        scope.launch {
            try {
                val buyIntentBundle: Bundle = service.getBuyIntent(
                    API_VERSION, activity.packageName, VIP_PRODUCT_ID, ITEM_TYPE_INAPP, ""
                )
                val pendingIntent = buyIntentBundle.getParcelable<android.app.PendingIntent>("BUY_INTENT")
                pendingIntent?.let {
                    val request = IntentSenderRequest.Builder(it.intentSender).build()
                    // باید در ترد اصلی اجرا شود
                    activity.runOnUiThread {
                        purchaseLauncher.launch(request)
                    }
                }
            } catch (e: IntentSender.SendIntentException) {
                // خطا در باز کردن صفحه‌ی پرداخت
            } catch (e: Exception) {
                // خطای عمومی ارتباط با سرویس مایکت
            }
        }
    }

    /** فقط برای تست داخلی در حالت توسعه — نباید در نسخه‌ی نهایی استفاده شود */
    fun debugSetVip(value: Boolean) {
        _isVip.value = value
    }
}
