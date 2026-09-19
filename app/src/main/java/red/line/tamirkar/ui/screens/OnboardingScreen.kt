package red.line.tamirkar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class OnboardingPage(val icon: ImageVector, val title: String, val description: String)

private val pages = listOf(
    OnboardingPage(
        icon = Icons.Filled.Handyman,
        title = "به دستیار تعمیرکار خوش آمدید",
        description = "همراه هوشمند شما برای مدیریت حرفه‌ای تعمیرگاه موبایل — مشتریان، فیش تعمیر، انبار و حسابداری، همه در یک اپ."
    ),
    OnboardingPage(
        icon = Icons.Filled.Build,
        title = "پذیرش سریع و پیگیری دقیق",
        description = "برای هر دستگاه یک فیش تعمیر بسازید، وضعیتش را دنبال کنید و هنگام آماده شدن، به‌صورت خودکار به مشتری پیامک بزنید."
    ),
    OnboardingPage(
        icon = Icons.Filled.WorkspacePremium,
        title = "با VIP همه‌چیز نامحدود می‌شود",
        description = "مدیریت انبار، حسابداری کامل، پایگاه‌داده عیب‌یابی و فیش نامحدود — هر وقت خواستید فعال کنید."
    )
)

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    val page = pages[currentPage]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            page.icon,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(24.dp))
        Text(page.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(12.dp))
        Text(page.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            pages.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentPage) 10.dp else 8.dp)
                        .background(
                            color = if (index == currentPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onFinish) { Text("رد کردن") }
            Button(onClick = {
                if (currentPage < pages.lastIndex) currentPage++ else onFinish()
            }) {
                Text(if (currentPage < pages.lastIndex) "بعدی" else "شروع کنید")
            }
        }
    }
}
