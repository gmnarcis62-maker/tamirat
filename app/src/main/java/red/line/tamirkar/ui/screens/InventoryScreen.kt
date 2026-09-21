package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.InventoryPart
import red.line.tamirkar.util.PersianDateUtils

@Composable
fun InventoryScreen(
    isVip: Boolean,
    parts: List<InventoryPart>,
    onAddPart: (String, String?, Int, Double, Double) -> Unit,
    onUpdatePart: (InventoryPart, String, String?, Int, Double, Double) -> Unit,
    onDeletePart: (InventoryPart) -> Unit,
    onUpgradeClick: () -> Unit
) {
    if (!isVip) {
        VipLockedContent(
            title = "مدیریت انبار قطعات",
            description = "این بخش مخصوص اعضای VIP است. با ارتقا حساب خود، موجودی قطعات، هشدار اتمام موجودی و قیمت خرید/فروش را مدیریت کنید.",
            onUpgradeClick = onUpgradeClick
        )
        return
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingPart by remember { mutableStateOf<InventoryPart?>(null) }
    var deletingPart by remember { mutableStateOf<InventoryPart?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "افزودن قطعه")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(parts) { part ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(part.name, style = MaterialTheme.typography.titleMedium)
                            Text("موجودی: ${PersianDateUtils.toPersianNumber(part.quantity)} عدد", style = MaterialTheme.typography.bodyMedium)
                            Text("قیمت فروش: ${PersianDateUtils.formatToman(part.sellPrice)}", style = MaterialTheme.typography.bodyMedium)
                            if (part.quantity <= part.lowStockThreshold) {
                                Text("⚠️ موجودی رو به اتمام است", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        Column {
                            IconButton(onClick = { editingPart = part }) { Icon(Icons.Filled.Edit, contentDescription = "ویرایش") }
                            IconButton(onClick = { deletingPart = part }) { Icon(Icons.Filled.Delete, contentDescription = "حذف") }
                        }
                    }
                }
            }
            if (parts.isEmpty()) {
                item {
                    red.line.tamirkar.ui.components.EmptyState(
                        icon = Icons.Filled.Inventory2,
                        title = "انبار خالی است",
                        description = "با دکمه‌ی + اولین قطعه را اضافه کنید"
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        PartFormDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { n, m, q, b, s ->
                onAddPart(n, m, q, b, s)
                showAddDialog = false
            }
        )
    }

    editingPart?.let { part ->
        PartFormDialog(
            initial = part,
            onDismiss = { editingPart = null },
            onConfirm = { n, m, q, b, s ->
                onUpdatePart(part, n, m, q, b, s)
                editingPart = null
            }
        )
    }

    deletingPart?.let { part ->
        AlertDialog(
            onDismissRequest = { deletingPart = null },
            title = { Text("حذف قطعه") },
            text = { Text("آیا از حذف «${part.name}» از انبار مطمئن هستید؟") },
            confirmButton = {
                TextButton(onClick = {
                    onDeletePart(part)
                    deletingPart = null
                }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { deletingPart = null }) { Text("انصراف") }
            }
        )
    }
}

@Composable
private fun PartFormDialog(
    initial: InventoryPart?,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Int, Double, Double) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var models by remember { mutableStateOf(initial?.compatibleModels ?: "") }
    var qty by remember { mutableStateOf(initial?.quantity?.toString() ?: "") }
    var buy by remember { mutableStateOf(initial?.purchasePrice?.toString() ?: "") }
    var sell by remember { mutableStateOf(initial?.sellPrice?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "قطعه جدید" else "ویرایش قطعه") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام قطعه") })
                OutlinedTextField(value = models, onValueChange = { models = it }, label = { Text("مدل‌های سازگار") })
                OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("موجودی") })
                OutlinedTextField(value = buy, onValueChange = { buy = it }, label = { Text("قیمت خرید") })
                OutlinedTextField(value = sell, onValueChange = { sell = it }, label = { Text("قیمت فروش") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(name, models.ifBlank { null }, qty.toIntOrNull() ?: 0, buy.toDoubleOrNull() ?: 0.0, sell.toDoubleOrNull() ?: 0.0)
            }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}

@Composable
fun VipLockedContent(title: String, description: String, onUpgradeClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(red.line.tamirkar.ui.theme.VipGold, red.line.tamirkar.ui.theme.VipGoldDark)
                    ),
                    androidx.compose.foundation.shape.CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(40.dp), tint = androidx.compose.ui.graphics.Color.White)
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, style = MaterialTheme.typography.bodyMedium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onUpgradeClick,
            colors = ButtonDefaults.buttonColors(containerColor = red.line.tamirkar.ui.theme.VipGoldDark)
        ) {
            Icon(Icons.Filled.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("مشاهده پلن VIP")
        }
    }
}
