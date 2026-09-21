package red.line.tamirkar.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.ShopProfile
import red.line.tamirkar.data.ThemeMode

@Composable
fun SettingsScreen(
    isVip: Boolean,
    shopProfile: ShopProfile,
    themeMode: ThemeMode,
    autoSmsEnabled: Boolean,
    onUpgradeClick: () -> Unit,
    onRateOnMyketClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSaveShopProfile: (ShopProfile) -> Unit,
    onPickLogo: (onPicked: (Uri) -> Unit) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onAutoSmsChange: (Boolean) -> Unit,
    onExportCsv: (String) -> Unit,
    onBackupExport: () -> Unit,
    onBackupRestore: () -> Unit,
    onOpenTechnicians: () -> Unit,
    onOpenReminders: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTermsOfService: () -> Unit
) {
    var shopName by remember(shopProfile) { mutableStateOf(shopProfile.shopName) }
    var ownerName by remember(shopProfile) { mutableStateOf(shopProfile.ownerName) }
    var phone by remember(shopProfile) { mutableStateOf(shopProfile.phone) }
    var address by remember(shopProfile) { mutableStateOf(shopProfile.address) }
    var logoUri by remember(shopProfile) { mutableStateOf(shopProfile.logoUri) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("تنظیمات", style = MaterialTheme.typography.titleLarge)

        if (red.line.tamirkar.AppConfig.TESTING_MODE_UNLOCK_ALL) {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Science, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "حالت آزمایشی فعال است — همه‌ی امکانات VIP برای تست باز هستند",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        if (!isVip) {
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(red.line.tamirkar.ui.theme.VipGold, red.line.tamirkar.ui.theme.VipGoldDark)
                        ),
                        androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                    ),
                color = androidx.compose.ui.graphics.Color.Transparent
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("ارتقا به نسخه VIP", style = MaterialTheme.typography.titleMedium, color = androidx.compose.ui.graphics.Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("فیش نامحدود، مدیریت انبار، حسابداری کامل، عیب‌یابی و حذف محدودیت‌ها", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onUpgradeClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White, contentColor = red.line.tamirkar.ui.theme.VipGoldDark)
                    ) {
                        Text("مشاهده پلن VIP", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            }
        }

        // ===== پروفایل فروشگاه =====
        SectionCard(title = "پروفایل فروشگاه (برای فاکتور)") {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    if (logoUri != null) {
                        coil.compose.AsyncImage(
                            model = logoUri,
                            contentDescription = "لوگو",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.Storefront, contentDescription = null)
                    }
                }
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = { onPickLogo { uri -> logoUri = uri.toString() } }) {
                    Text("انتخاب لوگو")
                }
            }
            OutlinedTextField(value = shopName, onValueChange = { shopName = it }, label = { Text("نام فروشگاه/تعمیرگاه") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ownerName, onValueChange = { ownerName = it }, label = { Text("نام مدیر") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("تلفن فروشگاه") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("آدرس") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = { onSaveShopProfile(ShopProfile(shopName, ownerName, phone, address, logoUri)) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("ذخیره پروفایل فروشگاه") }
        }

        // ===== ظاهر برنامه =====
        SectionCard(title = "ظاهر برنامه") {
            Column {
                ThemeOptionRow("خودکار (بر اساس سیستم)", themeMode == ThemeMode.SYSTEM) { onThemeModeChange(ThemeMode.SYSTEM) }
                ThemeOptionRow("روشن", themeMode == ThemeMode.LIGHT) { onThemeModeChange(ThemeMode.LIGHT) }
                ThemeOptionRow("تیره", themeMode == ThemeMode.DARK) { onThemeModeChange(ThemeMode.DARK) }
            }
        }

        // ===== اطلاع‌رسانی به مشتری =====
        SectionCard(title = "اطلاع‌رسانی به مشتری") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("ارسال خودکار پیامک")
                    Text("هنگام تغییر وضعیت فیش به «آماده تحویل»، پیامک به‌صورت خودکار ارسال شود", style = MaterialTheme.typography.bodyMedium)
                }
                Switch(checked = autoSmsEnabled, onCheckedChange = onAutoSmsChange)
            }
            Text("در صورت غیرفعال بودن، اپ پیامک با متن آماده باز می‌شود تا خودتان ارسال کنید.", style = MaterialTheme.typography.labelSmall)
        }

        // ===== مدیریت تیم =====
        SectionCard(title = "تیم و تکنسین‌ها") {
            SettingsRow(icon = Icons.Filled.Group, title = "مدیریت تکنسین‌ها", onClick = onOpenTechnicians)
            SettingsRow(icon = Icons.Filled.Event, title = "یادآوری‌ها و تقویم", onClick = onOpenReminders)
        }

        // ===== گزارش‌گیری =====
        SectionCard(title = "خروجی گزارش (Excel/CSV)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onExportCsv("customers") }, modifier = Modifier.weight(1f)) { Text("مشتریان") }
                OutlinedButton(onClick = { onExportCsv("tickets") }, modifier = Modifier.weight(1f)) { Text("فیش‌ها") }
                OutlinedButton(onClick = { onExportCsv("transactions") }, modifier = Modifier.weight(1f)) { Text("تراکنش‌ها") }
            }
        }

        // ===== پشتیبان‌گیری =====
        SectionCard(title = "پشتیبان‌گیری") {
            Text("از تمام اطلاعات برنامه یک فایل پشتیبان تهیه کنید یا فایل قبلی را بازیابی نمایید.", style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onBackupExport, modifier = Modifier.weight(1f)) { Text("تهیه بک‌آپ") }
                OutlinedButton(onClick = onBackupRestore, modifier = Modifier.weight(1f)) { Text("بازیابی") }
            }
            Text("⚠️ بازیابی، تمام اطلاعات فعلی را با اطلاعات فایل بک‌آپ جایگزین می‌کند.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
        }

        SettingsRow(icon = Icons.Filled.Star, title = "ثبت نظر در مایکت", onClick = onRateOnMyketClick)
        SettingsRow(icon = Icons.Filled.SupportAgent, title = "پشتیبانی", subtitle = "gmnarcis@gmail.com", onClick = onSupportClick)
        SettingsRow(icon = Icons.Filled.PrivacyTip, title = "حریم خصوصی", onClick = onOpenPrivacyPolicy)
        SettingsRow(icon = Icons.Filled.Gavel, title = "شرایط استفاده", onClick = onOpenTermsOfService)

        Spacer(Modifier.height(8.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        Text("درباره برنامه", style = MaterialTheme.typography.titleMedium)
        Text("توسعه‌دهنده: تیم نرم‌افزاری ردلاین سافت البرز")
        Text("مدیریت: مهندس مهدی رضایی")
        Text("نسخه: ۱.۰.۰")
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ThemeOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            }
            TextButton(onClick = onClick) { Text("باز کردن") }
        }
    }
}
