package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.data.TicketStatus

data class DashboardStat(val title: String, val value: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

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
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("داشبورد تعمیرگاه", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (!isVip) {
                AssistChip(onClick = onUpgradeClick, label = { Text("ارتقا به VIP") })
            } else {
                AssistChip(onClick = {}, label = { Text("عضو VIP") }, leadingIcon = {
                    Icon(Icons.Filled.Star, contentDescription = null)
                })
            }
        }

        Spacer(Modifier.height(16.dp))

        val stats = listOf(
            DashboardStat("فیش‌های فعال", red.line.tamirkar.util.PersianDateUtils.toPersianNumber(activeTicketCount), Icons.Filled.Build),
            DashboardStat("درآمد امروز", red.line.tamirkar.util.PersianDateUtils.formatToman(todayIncome), Icons.Filled.Payments),
            DashboardStat("موجودی کم", "${red.line.tamirkar.util.PersianDateUtils.toPersianNumber(lowStockCount)} قطعه", Icons.Filled.Warning),
            DashboardStat("وضعیت اشتراک", if (isVip) "VIP" else "رایگان", Icons.Filled.WorkspacePremium)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(stats) { stat ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Icon(stat.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text(stat.value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(stat.title, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Button(
            onClick = onNewTicketClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("ثبت پذیرش جدید")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onTroubleshootingClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Search, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("پایگاه‌داده عیب‌یابی گوشی")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onAccountingClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Payments, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("حسابداری و صندوق")
        }
    }
}
