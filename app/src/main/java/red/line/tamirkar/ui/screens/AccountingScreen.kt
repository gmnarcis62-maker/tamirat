package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Transaction
import red.line.tamirkar.data.TransactionCategory
import red.line.tamirkar.data.TransactionType
import red.line.tamirkar.util.PersianDateUtils

@Composable
fun AccountingScreen(
    isVip: Boolean,
    transactions: List<Transaction>,
    todayIncome: Double,
    todayExpense: Double,
    monthIncome: Double,
    monthExpense: Double,
    onAddTransaction: (TransactionType, TransactionCategory, Double, String?) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onUpgradeClick: () -> Unit
) {
    if (!isVip) {
        VipLockedContent(
            title = "حسابداری و صندوق",
            description = "ثبت درآمد و هزینه، مشاهده سود روزانه/ماهانه و گزارش کامل مالی تعمیرگاه، مخصوص اعضای VIP است.",
            onUpgradeClick = onUpgradeClick
        )
        return
    }

    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "ثبت تراکنش جدید")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("حسابداری و صندوق", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(
                        title = "سود امروز",
                        value = todayIncome - todayExpense,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "سود این ماه",
                        value = monthIncome - monthExpense,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("خلاصه امروز", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            LabeledAmount("درآمد", todayIncome, isIncome = true)
                            LabeledAmount("هزینه", todayExpense, isIncome = false)
                        }
                    }
                }
            }

            item {
                Text("تراکنش‌ها", style = MaterialTheme.typography.titleMedium)
            }

            items(transactions) { transaction ->
                TransactionRow(transaction, onDelete = { onDeleteTransaction(transaction) })
            }

            if (transactions.isEmpty()) {
                item {
                    Text("هنوز تراکنشی ثبت نشده است", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }

    if (showDialog) {
        AddTransactionDialog(
            onDismiss = { showDialog = false },
            onConfirm = { type, category, amount, note ->
                onAddTransaction(type, category, amount, note)
                showDialog = false
            }
        )
    }
}

@Composable
private fun SummaryCard(title: String, value: Double, modifier: Modifier = Modifier) {
    val color = if (value >= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
    ElevatedCard(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(red.line.tamirkar.util.PersianDateUtils.formatToman(value), style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LabeledAmount(label: String, amount: Double, isIncome: Boolean) {
    val color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (isIncome) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
        Text(red.line.tamirkar.util.PersianDateUtils.formatToman(amount), color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
    val isIncome = transaction.type == TransactionType.INCOME
    val color = if (isIncome) Color(0xFF2E7D32) else Color(0xFFC62828)

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(transaction.category.label, style = MaterialTheme.typography.bodyLarge)
                transaction.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                Text(PersianDateUtils.formatDateTime(transaction.date), style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (isIncome) "+" else "-"}${red.line.tamirkar.util.PersianDateUtils.toPersianDigits("%,.0f".format(transaction.amount))}",
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onDelete) { Text("حذف") }
            }
        }
    }
}

@Composable
private fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (TransactionType, TransactionCategory, Double, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.INCOME) }
    var selectedCategory by remember { mutableStateOf(TransactionCategory.REPAIR_INCOME) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val categoriesForType = TransactionCategory.entries.filter { it.type == selectedType }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ثبت تراکنش جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == TransactionType.INCOME,
                        onClick = {
                            selectedType = TransactionType.INCOME
                            selectedCategory = TransactionCategory.REPAIR_INCOME
                        },
                        label = { Text("درآمد") }
                    )
                    FilterChip(
                        selected = selectedType == TransactionType.EXPENSE,
                        onClick = {
                            selectedType = TransactionType.EXPENSE
                            selectedCategory = TransactionCategory.PART_PURCHASE
                        },
                        label = { Text("هزینه") }
                    )
                }

                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("دسته‌بندی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categoriesForType.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat.label) }, onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            })
                        }
                    }
                }

                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("مبلغ (تومان)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("توضیحات (اختیاری)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                if (amountValue > 0) {
                    onConfirm(selectedType, selectedCategory, amountValue, note.ifBlank { null })
                }
            }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
