package red.line.tamirkar.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.SecureRandom

/**
 * تولید و نگهداری امن کلید رمزنگاری دیتابیس محلی (SQLCipher).
 *
 * کلید یک‌بار (در اولین اجرای برنامه) به‌صورت تصادفی تولید می‌شود و با استفاده از
 * Android Keystore (از طریق EncryptedSharedPreferences) به‌صورت امن ذخیره می‌شود.
 * این کلید هیچ‌وقت به‌صورت متن ساده جایی ذخیره یا لاگ نمی‌شود.
 *
 * نتیجه: حتی اگر فایل دیتابیس (tamirkar.db) از گوشی کاربر (روت‌شده یا با دسترسی فیزیکی)
 * استخراج شود، بدون این کلید قابل خواندن نیست.
 */
object DatabasePassphraseManager {

    private const val PREFS_NAME = "tamirkar_secure_prefs"
    private const val KEY_DB_PASSPHRASE = "db_passphrase"

    fun getOrCreatePassphrase(context: Context): ByteArray {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val existing = encryptedPrefs.getString(KEY_DB_PASSPHRASE, null)
        if (existing != null) {
            return existing.toByteArray(Charsets.ISO_8859_1)
        }

        val random = SecureRandom()
        val newKeyBytes = ByteArray(32)
        random.nextBytes(newKeyBytes)
        val newKeyString = String(newKeyBytes, Charsets.ISO_8859_1)

        encryptedPrefs.edit().putString(KEY_DB_PASSPHRASE, newKeyString).apply()
        return newKeyBytes
    }
}
