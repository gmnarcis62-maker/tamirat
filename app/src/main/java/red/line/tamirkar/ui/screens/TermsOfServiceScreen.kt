@file:OptIn(ExperimentalMaterial3Api::class)

package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("شرایط استفاده") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("شرایط استفاده از دستیار تعمیرکار", style = MaterialTheme.typography.titleLarge)
            Text("آخرین بروزرسانی: ۱۴۰۴", style = MaterialTheme.typography.bodyMedium)

            PolicySectionTos(
                "نحوه استفاده از برنامه",
                "این برنامه ابزاری برای مدیریت داخلی کسب‌وکار تعمیرگاه موبایل است. تمام اطلاعاتی که وارد می‌کنید مسئولیتش با شماست و باید از برنامه برای مقاصد قانونی استفاده کنید."
            )
            PolicySectionTos(
                "اشتراک VIP",
                "خرید اشتراک VIP از طریق درگاه پرداخت مایکت و طبق قوانین و سیاست بازگشت وجه مایکت انجام می‌شود. امکانات نسخه رایگان و VIP ممکن است در نسخه‌های بعدی تغییر کند."
            )
            PolicySectionTos(
                "محدودیت مسئولیت",
                "این برنامه «همان‌طور که هست» ارائه می‌شود. مسئولیت پشتیبان‌گیری منظم از اطلاعات بر عهده‌ی شماست. توسعه‌دهنده در قبال از دست رفتن اطلاعات یا استفاده نادرست مسئولیتی نمی‌پذیرد."
            )
            PolicySectionTos(
                "تماس با ما",
                "gmnarcis@gmail.com"
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicySectionTos(title: String, body: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}