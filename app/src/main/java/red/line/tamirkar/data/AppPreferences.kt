package red.line.tamirkar.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "tamirkar_settings")

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class ShopProfile(
    val shopName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val address: String = "",
    val logoUri: String? = null
)

/**
 * مدیریت تنظیمات پایدار برنامه با DataStore:
 * - تم روشن/تاریک/خودکار
 * - پروفایل فروشگاه (برای چاپ فاکتور)
 * - فعال/غیرفعال بودن ارسال خودکار پیامک به مشتری
 * - وضعیت اشتراک VIP (تا با بستن برنامه از بین نرود)
 */
class AppPreferences(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val SHOP_NAME = stringPreferencesKey("shop_name")
        val OWNER_NAME = stringPreferencesKey("owner_name")
        val SHOP_PHONE = stringPreferencesKey("shop_phone")
        val SHOP_ADDRESS = stringPreferencesKey("shop_address")
        val SHOP_LOGO_URI = stringPreferencesKey("shop_logo_uri")
        val AUTO_SMS_ENABLED = booleanPreferencesKey("auto_sms_enabled")
        val IS_VIP = booleanPreferencesKey("is_vip")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.THEME_MODE]) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name.lowercase() }
    }

    val shopProfile: Flow<ShopProfile> = context.dataStore.data.map { prefs ->
        ShopProfile(
            shopName = prefs[Keys.SHOP_NAME] ?: "",
            ownerName = prefs[Keys.OWNER_NAME] ?: "",
            phone = prefs[Keys.SHOP_PHONE] ?: "",
            address = prefs[Keys.SHOP_ADDRESS] ?: "",
            logoUri = prefs[Keys.SHOP_LOGO_URI]
        )
    }

    suspend fun saveShopProfile(profile: ShopProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOP_NAME] = profile.shopName
            prefs[Keys.OWNER_NAME] = profile.ownerName
            prefs[Keys.SHOP_PHONE] = profile.phone
            prefs[Keys.SHOP_ADDRESS] = profile.address
            profile.logoUri?.let { prefs[Keys.SHOP_LOGO_URI] = it }
        }
    }

    val autoSmsEnabled: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_SMS_ENABLED] ?: false }

    suspend fun setAutoSmsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_SMS_ENABLED] = enabled }
    }

    val isVip: Flow<Boolean> = context.dataStore.data.map { it[Keys.IS_VIP] ?: false }

    suspend fun setIsVip(value: Boolean) {
        context.dataStore.edit { it[Keys.IS_VIP] = value }
    }

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    suspend fun setOnboardingDone(value: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = value }
    }
}
