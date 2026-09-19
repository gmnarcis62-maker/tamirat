package red.line.tamirkar

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import red.line.tamirkar.billing.MyketBillingManager
import red.line.tamirkar.data.AppPreferences
import red.line.tamirkar.data.ThemeMode
import red.line.tamirkar.ui.navigation.TamirkarNavGraph
import red.line.tamirkar.ui.theme.TamirkarTheme
import red.line.tamirkar.util.BackupManager
import red.line.tamirkar.util.SmsHelper
import red.line.tamirkar.viewmodel.MainViewModel
import red.line.tamirkar.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var billingManager: MyketBillingManager
    private lateinit var preferences: AppPreferences

    // ===== انتخاب تصویر (برای لوگوی فروشگاه یا عکس قبل/بعد تعمیر) =====
    private var pendingImageCallback: ((Uri) -> Unit)? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { pendingImageCallback?.invoke(it) }
        pendingImageCallback = null
    }
    fun pickImage(onPicked: (Uri) -> Unit) {
        pendingImageCallback = onPicked
        pickImageLauncher.launch("image/*")
    }

    // ===== بازیابی بک‌آپ از فایل JSON =====
    private var pendingRestoreCallback: ((Uri) -> Unit)? = null
    private val restoreLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { pendingRestoreCallback?.invoke(it) }
        pendingRestoreCallback = null
    }
    fun pickBackupFile(onPicked: (Uri) -> Unit) {
        pendingRestoreCallback = onPicked
        restoreLauncher.launch(arrayOf("application/json"))
    }

    // ===== مجوز پیامک خودکار =====
    private var pendingSms: Pair<String, String>? = null
    private val smsPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pendingSms?.let { (phone, msg) -> SmsHelper.sendAutomatically(phone, msg) }
        pendingSms = null
    }

    // ===== مجوز اعلان‌ها (اندروید ۱۳+) =====
    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    fun sendReadySms(phone: String, message: String, auto: Boolean) {
        if (!auto) {
            SmsHelper.openSmsApp(this, phone, message)
            return
        }
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            SmsHelper.sendAutomatically(phone, message)
        } else {
            pendingSms = phone to message
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    fun ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            if (!granted) notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun shareFile(uri: Uri, mimeType: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "اشتراک‌گذاری فایل"))
    }

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(TamirkarApp.updatePersianLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        ensureNotificationPermission()

        preferences = AppPreferences(this)
        billingManager = MyketBillingManager(this)
        billingManager.queryPurchaseStatus()

        val app = application as TamirkarApp
        val repository = red.line.tamirkar.data.TamirkarRepository(
            customerDao = app.database.customerDao(),
            ticketDao = app.database.repairTicketDao(),
            inventoryDao = app.database.inventoryDao(),
            troubleshootingDao = app.database.troubleshootingDao(),
            transactionDao = app.database.transactionDao(),
            technicianDao = app.database.technicianDao(),
            reminderDao = app.database.reminderDao()
        )
        val factory = ViewModelFactory(repository, preferences)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = factory)
            val themeMode by viewModel.themeMode.collectAsState()
            val autoSmsEnabled by viewModel.autoSmsEnabled.collectAsState()
            val shopProfile by viewModel.shopProfile.collectAsState()
            val onboardingDone by viewModel.onboardingDone.collectAsState()

            // مشاهده وضعیت خرید VIP از مایکت و همگام‌سازی با DataStore
            LaunchedEffect(Unit) {
                billingManager.isVip.collect { vip -> viewModel.setVipStatus(vip) }
            }

            // ارسال خودکار پیامک هنگام آماده شدن دستگاه (در صورت فعال بودن گزینه در تنظیمات)
            LaunchedEffect(autoSmsEnabled) {
                viewModel.ticketReadyEvent.collect { ticket ->
                    if (autoSmsEnabled) {
                        val customer = viewModel.customers.value.find { it.id == ticket.customerId }
                        val message = SmsHelper.readyMessage(customer?.fullName ?: "مشتری گرامی", ticket, shopProfile.shopName.ifBlank { "دستیار تعمیرکار" })
                        customer?.let { sendReadySms(it.phoneNumber, message, auto = true) }
                    }
                }
            }

            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            TamirkarTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        if (!onboardingDone) {
                            red.line.tamirkar.ui.screens.OnboardingScreen(onFinish = { viewModel.completeOnboarding() })
                        } else {
                        TamirkarNavGraph(
                            viewModel = viewModel,
                            onUpgradeClick = { billingManager.launchVipPurchase() },
                            onRateOnMyketClick = { openMyketPage() },
                            onSupportClick = { sendSupportEmail() },
                            onPickImage = { callback -> pickImage(callback) },
                            onGeneratePdfInvoice = { ticket, customerName, customerPhone ->
                                viewModel.setBusy(true)
                                lifecycleScope.launch {
                                    try {
                                        val uri = red.line.tamirkar.util.PdfInvoiceGenerator.generate(this@MainActivity, ticket, customerName, customerPhone, shopProfile)
                                        shareFile(uri, "application/pdf")
                                        viewModel.notify("فاکتور PDF آماده شد")
                                    } catch (e: Exception) {
                                        viewModel.notify("خطا در تولید فاکتور PDF")
                                    } finally {
                                        viewModel.setBusy(false)
                                    }
                                }
                            },
                            onExportCsv = { type ->
                                viewModel.setBusy(true)
                                lifecycleScope.launch {
                                    try {
                                        val uri = when (type) {
                                            "customers" -> red.line.tamirkar.util.CsvExporter.exportCustomers(this@MainActivity, viewModel.customers.value)
                                            "tickets" -> red.line.tamirkar.util.CsvExporter.exportTickets(this@MainActivity, viewModel.tickets.value) { id ->
                                                viewModel.customers.value.find { it.id == id }?.fullName ?: "نامشخص"
                                            }
                                            else -> red.line.tamirkar.util.CsvExporter.exportTransactions(this@MainActivity, viewModel.transactions.value)
                                        }
                                        shareFile(uri, "text/csv")
                                        viewModel.notify("فایل خروجی آماده شد")
                                    } catch (e: Exception) {
                                        viewModel.notify("خطا در تولید فایل خروجی")
                                    } finally {
                                        viewModel.setBusy(false)
                                    }
                                }
                            },
                            onBackupExport = {
                                viewModel.setBusy(true)
                                lifecycleScope.launch {
                                    try {
                                        val data = viewModel.currentBackupData()
                                        val uri = BackupManager.export(this@MainActivity, data)
                                        shareFile(uri, "application/json")
                                        viewModel.notify("فایل پشتیبان آماده شد")
                                    } catch (e: Exception) {
                                        viewModel.notify("خطا در تهیه فایل پشتیبان")
                                    } finally {
                                        viewModel.setBusy(false)
                                    }
                                }
                            },
                            onBackupRestore = {
                                pickBackupFile { uri ->
                                    viewModel.setBusy(true)
                                    lifecycleScope.launch {
                                        try {
                                            val data = BackupManager.import(this@MainActivity, uri)
                                            if (data != null) {
                                                viewModel.restoreBackup(data) {}
                                            } else {
                                                viewModel.notify("فایل بک‌آپ نامعتبر است")
                                            }
                                        } catch (e: Exception) {
                                            viewModel.notify("خطا در بازیابی بک‌آپ")
                                        } finally {
                                            viewModel.setBusy(false)
                                        }
                                    }
                                }
                            },
                            onSendReadySms = { phone, message ->
                                sendReadySms(phone, message, auto = false)
                            },
                            onScheduleReminder = { reminder ->
                                red.line.tamirkar.util.ReminderScheduler.schedule(this@MainActivity, reminder)
                            }
                        )
                        }
                    }
                }
            }
        }
    }

    /** باز کردن صفحه برنامه در مایکت برای ثبت نظر و امتیاز */
    private fun openMyketPage() {
        val uri = Uri.parse("myket://comment?id=$packageName")
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        } catch (e: ActivityNotFoundException) {
            val webUri = Uri.parse("https://myket.ir/app/$packageName")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    /** ارسال ایمیل پشتیبانی */
    private fun sendSupportEmail() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:gmnarcis@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی دستیار تعمیرکار")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // در صورت نبود اپ ایمیل، می‌توان Snackbar نمایش داد
        }
    }
}