package red.line.tamirkar.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Customer
import red.line.tamirkar.data.InventoryPart
import red.line.tamirkar.util.PersianDateUtils
import java.util.Calendar

@Composable
fun NewTicketScreen(
    customers: List<Customer>,
    inventoryParts: List<InventoryPart>,
    freeTierLimitReached: Boolean,
    onSubmit: (customerId: Long, brand: String, model: String, problem: String, accessories: String?, price: Double, expectedDelivery: Long?, beforePhotoUri: String?) -> Unit,
    onPickImage: (onPicked: (Uri) -> Unit) -> Unit,
    onUpgradeClick: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var accessories by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var beforePhoto by remember { mutableStateOf<Uri?>(null) }
    var expectedDeliveryDays by remember { mutableStateOf<Int?>(null) }

    // ===== محاسبه‌گر هوشمند قیمت (قطعه + اجرت) =====
    var showCalculator by remember { mutableStateOf(false) }
    var selectedPart by remember { mutableStateOf<InventoryPart?>(null) }
    var laborFee by remember { mutableStateOf("") }
    var partMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("پذیرش دستگاه جدید", style = MaterialTheme.typography.titleLarge)

        if (freeTierLimitReached) {
            ElevatedCard {
                Column(Modifier.padding(16.dp)) {
                    Text("شما به سقف مجاز فیش فعال در نسخه رایگان رسیده‌اید.")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = onUpgradeClick) { Text("ارتقا به نسخه VIP برای ثبت نامحدود") }
                }
            }
            return@Column
        }

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selectedCustomer?.fullName ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("انتخاب مشتری") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                customers.forEach { c ->
                    DropdownMenuItem(text = { Text("${c.fullName} - ${c.phoneNumber}") }, onClick = {
                        selectedCustomer = c
                        expanded = false
                    })
                }
            }
        }

        OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("برند دستگاه") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("مدل دستگاه") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("شرح ایراد") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        OutlinedTextField(value = accessories, onValueChange = { accessories = it }, label = { Text("لوازم همراه (اختیاری)") }, modifier = Modifier.fillMaxWidth())

        // دکمه عکس قبل از تعمیر
        OutlinedButton(
            onClick = { onPickImage { uri -> beforePhoto = uri } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.AddAPhoto, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (beforePhoto == null) "افزودن عکس قبل از تعمیر (اختیاری)" else "عکس ثبت شد ✓")
        }

        // تاریخ موعد تحویل تخمینی
        Text("موعد تخمینی تحویل", style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "فردا", 3 to "۳ روز دیگر", 7 to "هفته دیگر").forEach { (days, label) ->
                FilterChip(
                    selected = expectedDeliveryDays == days,
                    onClick = { expectedDeliveryDays = if (expectedDeliveryDays == days) null else days },
                    label = { Text(label) }
                )
            }
        }

        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("قیمت توافقی (تومان)") }, modifier = Modifier.fillMaxWidth())

        TextButton(onClick = { showCalculator = !showCalculator }) {
            Icon(Icons.Filled.Calculate, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("محاسبه‌گر قیمت (قطعه + اجرت)")
        }

        if (showCalculator) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(expanded = partMenuExpanded, onExpandedChange = { partMenuExpanded = it }) {
                        OutlinedTextField(
                            value = selectedPart?.let { "${it.name} (${PersianDateUtils.formatToman(it.sellPrice)})" } ?: "بدون قطعه",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("قطعه از انبار (اختیاری)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = partMenuExpanded, onDismissRequest = { partMenuExpanded = false }) {
                            DropdownMenuItem(text = { Text("بدون قطعه") }, onClick = { selectedPart = null; partMenuExpanded = false })
                            inventoryParts.forEach { part ->
                                DropdownMenuItem(text = { Text("${part.name} — ${PersianDateUtils.formatToman(part.sellPrice)}") }, onClick = {
                                    selectedPart = part
                                    partMenuExpanded = false
                                })
                            }
                        }
                    }
                    OutlinedTextField(value = laborFee, onValueChange = { laborFee = it }, label = { Text("اجرت تعمیر (تومان)") }, modifier = Modifier.fillMaxWidth())

                    val total = (selectedPart?.sellPrice ?: 0.0) + (laborFee.toDoubleOrNull() ?: 0.0)
                    Text("جمع پیشنهادی: ${PersianDateUtils.formatToman(total)}", style = MaterialTheme.typography.titleMedium)
                    Button(onClick = { price = total.toInt().toString() }, modifier = Modifier.fillMaxWidth()) {
                        Text("اعمال به قیمت توافقی")
                    }
                }
            }
        }

        Button(
            onClick = {
                selectedCustomer?.let {
                    val expectedTs = expectedDeliveryDays?.let { d ->
                        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, d) }.timeInMillis
                    }
                    onSubmit(
                        it.id, brand, model, problem, accessories.ifBlank { null },
                        price.toDoubleOrNull() ?: 0.0, expectedTs, beforePhoto?.toString()
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedCustomer != null && brand.isNotBlank() && model.isNotBlank() && problem.isNotBlank()
        ) {
            Text("ثبت فیش پذیرش")
        }
    }
}
