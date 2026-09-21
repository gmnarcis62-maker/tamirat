package red.line.tamirkar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import red.line.tamirkar.ui.components.GradientHero
import red.line.tamirkar.ui.theme.StatCardShape
import red.line.tamirkar.util.PersianDateUtils

private data class DashboardStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val tint: Color
)

private data class QuickAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    activeTicketCount: Int,
    todayIncome: Double,
    lowStockCount: Int,
    isVip: Boolean,
    onNewTicketClick: () -> Unit,
    onTroubleshootingClick: () -> Unit,
    onAccountingClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScrollWorkaround()
    ) {
        GradientHero(
            title = "سلام 👋",
            subtitle = "خلاصه‌ی امروز تعمیرگاه شما",
            trailing = { TrailingVipChip(isVip, onUpgradeClick) }
        )

        Column(modifier = Modifier.padding(16.dp)) {
            val stats = listOf(
                DashboardStat(
                    "فیش‌های فعال", PersianDateUtils.toPersianNumber(activeTicketCount),
                    Icons.Filled.Build, Color(0xFF1E88E5)
                ),
                DashboardStat(
                    "درآمد امروز", PersianDateUtils.formatToman(todayIncome),
                    Icons.Filled.Payments, Color(0xFF43A047)
                ),
                DashboardStat(
                    "موجودی کم", "${PersianDateUtils.toPersianNumber(lowStockCount)} قطعه",
                    Icons.Filled.Warning, Color(0xFFF9A825)
                ),
                DashboardStat(
                    "وضعیت اشتراک", if (isVip) "VIP" else "رایگان",
                    Icons.Filled.WorkspacePremium, Color(0xFFC79100)
                )
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.height(220.dp)
            ) {
                items(stats) { stat -> StatCard(stat) }
            }

            Spacer(Modifier.height(24.dp))
            Text("دسترسی سریع", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            val actions = listOf(
                QuickAction("پذیرش جدید", Icons.Filled.Add, onNewTicketClick),
                QuickAction("عیب‌یابی", Icons.Filled.Search, onTroubleshootingClick),
                QuickAction("حسابداری", Icons.Filled.AccountBalanceWallet, onAccountingClick)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                actions.forEach { action ->
                    QuickActionButton(action, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TrailingVipChip(isVip: Boolean, onUpgradeClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isVip) Color(0x33FFC107) else Color.White.copy(alpha = 0.15f),
        onClick = onUpgradeClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (isVip) Icons.Filled.WorkspacePremium else Icons.Filled.LockOpen,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (isVip) "عضو VIP" else "ارتقا به VIP",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun StatCard(stat: DashboardStat) {
    Surface(
        shape = StatCardShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().fillMaxHeight()
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(stat.tint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(stat.icon, contentDescription = null, tint = stat.tint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(stat.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(stat.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickActionButton(action: QuickAction, modifier: Modifier = Modifier) {
    Surface(
        onClick = action.onClick,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier.height(84.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(6.dp))
            Text(
                action.label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
    }
}

// اسکرول ساده برای وقتی محتوا از صفحه بلندتر است (در صفحات کوچک)
@Composable
private fun Modifier.verticalScrollWorkaround(): Modifier {
    val scrollState = androidx.compose.foundation.rememberScrollState()
    return this.then(androidx.compose.foundation.verticalScroll(scrollState))
}
