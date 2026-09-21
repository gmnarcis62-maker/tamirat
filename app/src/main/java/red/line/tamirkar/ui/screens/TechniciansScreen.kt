@file:OptIn(ExperimentalMaterial3Api::class)

package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Technician

@Composable
fun TechniciansScreen(
    isVip: Boolean,
    technicians: List<Technician>,
    onBack: () -> Unit,
    onAdd: (String, String) -> Unit,
    onUpdate: (Technician, String, String, Boolean) -> Unit,
    onDelete: (Technician) -> Unit,
    onUpgradeClick: () -> Unit
) {
    if (!isVip) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("تکنسین‌ها") },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } }
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                VipLockedContent(
                    title = "مدیریت تکنسین‌ها",
                    description = "افزودن چند تکنسین و واگذاری فیش‌ها به هرکدام، مخصوص اعضای VIP است.",
                    onUpgradeClick = onUpgradeClick
                )
            }
        }
        return
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Technician?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تکنسین‌ها") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "افزودن تکنسین")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(technicians) { tech ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(tech.name, style = MaterialTheme.typography.titleMedium)
                            Text(if (tech.isOwner) "مدیر تعمیرگاه" else "تکنسین", style = MaterialTheme.typography.bodyMedium)
                            if (!tech.active) Text("غیرفعال", color = MaterialTheme.colorScheme.error)
                        }
                        if (!tech.isOwner) {
                            IconButton(onClick = { editing = tech }) { Icon(Icons.Filled.Edit, contentDescription = "ویرایش") }
                            IconButton(onClick = { onDelete(tech) }) { Icon(Icons.Filled.Delete, contentDescription = "حذف") }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TechnicianDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, pin ->
                onAdd(name, pin)
                showAddDialog = false
            }
        )
    }

    editing?.let { tech ->
        TechnicianDialog(
            initial = tech,
            onDismiss = { editing = null },
            onConfirm = { name, pin ->
                onUpdate(tech, name, pin, tech.active)
                editing = null
            }
        )
    }
}

@Composable
private fun TechnicianDialog(
    initial: Technician?,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var pin by remember { mutableStateOf(initial?.pinCode ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "تکنسین جدید" else "ویرایش تکنسین") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام تکنسین") })
                OutlinedTextField(
                    value = pin,
                    onValueChange = { if (it.length <= 4) pin = it },
                    label = { Text("کد پین ۴ رقمی") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword)
                )
                Text("این پین فقط برای تعویض کاربر روی همین دستگاه است، نه یک سیستم ورود آنلاین.", style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, pin) }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}