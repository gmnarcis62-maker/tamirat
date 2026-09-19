package red.line.tamirkar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Customer::class,
        RepairTicket::class,
        InventoryPart::class,
        TroubleshootingGuide::class,
        Transaction::class,
        Technician::class,
        Reminder::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun repairTicketDao(): RepairTicketDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun troubleshootingDao(): TroubleshootingDao
    abstract fun transactionDao(): TransactionDao
    abstract fun technicianDao(): TechnicianDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }

        private fun build(context: Context): AppDatabase {
            val scope = CoroutineScope(Dispatchers.IO)
            val passphrase = DatabasePassphraseManager.getOrCreatePassphrase(context)
            val factory = net.sqlcipher.database.SupportFactory(passphrase)
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tamirkar.db"
            )
                .openHelperFactory(factory) // رمزنگاری کامل دیتابیس با SQLCipher
                // پروژه هنوز منتشر نشده؛ در نسخه‌ی نهایی حتماً باید Migration واقعی نوشته شود
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        scope.launch {
                            INSTANCE?.let { database ->
                                // درج خودکار پایگاه‌داده‌ی عیب‌یابی هنگام اولین اجرا
                                if (database.troubleshootingDao().count() == 0) {
                                    database.troubleshootingDao().insertAll(TroubleshootingSeedData.defaultGuides())
                                }
                                // ایجاد یک حساب پیش‌فرض «مدیر» هنگام اولین اجرا (پین پیش‌فرض 0000 - در تنظیمات قابل تغییر است)
                                if (database.technicianDao().count() == 0) {
                                    database.technicianDao().insert(
                                        Technician(name = "مدیر تعمیرگاه", pinCode = "0000", isOwner = true)
                                    )
                                }
                            }
                        }
                    }
                })
                .build()
        }
    }
}
