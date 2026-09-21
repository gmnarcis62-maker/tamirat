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
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حریم خصوصی") },
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
            Text("سیاست حریم خصوصی دستیار تعمیرکار", style = MaterialTheme.typography.titleLarge)
            Text("آخرین بروزرسانی: ۱۴۰۴", style = MaterialTheme.typography.bodyMedium)

            PolicySection(
                title = "چه اطلاعاتی ذخیره می‌شود؟",
                body = "این برنامه اطلاعات مشتریان (نام، شماره تماس، آدرس)، فیش‌های تعمیر، اطلاعات انبار قطعات و تراکنش‌های مالی را برای استفاده‌ی شما به‌عنوان صاحب تعمیرگاه ذخیره می‌کند. تمام این اطلاعات فقط به‌صورت محلی روی گوشی شما ذخیره می‌شود و به هیچ سروری ارسال نمی‌شود، مگر در مواردی که خودتان اقدام به اشتراک‌گذاری (مثل ارسال فاکتور یا پشتیبان‌گیری) کنید."
            )
            PolicySection(
                title = "دسترسی‌های برنامه",
                body = "برنامه ممکن است برای موارد زیر از شما مجوز بخواهد: ارسال پیامک (برای اطلاع‌رسانی به مشتری)، نمایش اعلان (برای یادآوری‌ها)، و دسترسی به گالری (برای انتخاب لوگو یا عکس تعمیر). هیچ‌کدام از این دسترسی‌ها بدون تأیید شما فعال نمی‌شوند."
            )
            PolicySection(
                title = "اشتراک‌گذاری اطلاعات",
                body = "اطلاعات مشتریان شما توسط تیم ردلاین سافت البرز جمع‌آوری، فروخته یا به اشخاص ثالث ارائه نمی‌شود. مسئولیت رعایت قوانین حفظ حریم خصوصی مشتریان خودتان (مثلاً کسب رضایت برای نگهداری شماره تماس) بر عهده‌ی صاحب تعمیرگاه (کاربر این برنامه) است."
            )
            PolicySection(
                title = "خرید درون‌برنامه‌ای (VIP)",
                body = "خرید اشتراک VIP از طریق سرویس پرداخت مایکت انجام می‌شود و اطلاعات پرداخت شما مستقیماً توسط مایکت مدیریت می‌شود؛ این برنامه به اطلاعات کارت بانکی شما دسترسی ندارد."
            )
            PolicySection(
                title = "تماس با ما",
                body = "در صورت هرگونه سؤال درباره‌ی حریم خصوصی، از طریق ایمیل gmnarcis@gmail.com با تیم پشتیبانی در تماس باشید."
            )

            Text(
                "⚠️ یادداشت برای تیم توسعه: این متن یک پیش‌نویس اولیه است. پیش از انتشار در مایکت، حتماً آن را با یک مشاور حقوقی بررسی کنید و طبق الزامات مایکت، نسخه‌ی وب همین متن را (فایل PRIVACY_POLICY.md پیوست پروژه) روی یک آدرس اینترنتی عمومی میزبانی کرده و لینکش را در پنل توسعه‌دهندگان مایکت ثبت کنید.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicySection(title: String, body: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(4.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium)
    }
}