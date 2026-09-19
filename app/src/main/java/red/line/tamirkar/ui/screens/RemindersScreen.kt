package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Reminder
import red.line.tamirkar.util.PersianDateUtils
import java.util.Calendar

@Composable
fun RemindersScreen(
    reminders: List<Reminder>,
    onBack: () -> Unit,
    onAdd: (String, String?, Long) -> Unit,
    onComplete: (Reminder) -> Unit,
    onDelete: (Reminder) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("یادآوری‌ها و تقویم") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "یادآوری جدید")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders) { reminder ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(reminder.title, style = MaterialTheme.typography.titleMedium)
                            reminder.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            Text(PersianDateUtils.formatDateTime(reminder.dateTimeMillis), style = MaterialTheme.typography.labelSmall)
                        }
                        IconButton(onClick = { onComplete(reminder) }) { Icon(Icons.Filled.Check, contentDescription = "انجام شد") }
                        IconButton(onClick = { onDelete(reminder) }) { Icon(Icons.Filled.Delete, contentDescription = "حذف") }
                    }
                }
            }
            if (reminders.isEmpty()) {
                item { Text("یادآوری فعالی وجود ندارد", style = MaterialTheme.typography.bodyMedium) }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, note, dateTime ->
                onAdd(title, note, dateTime)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    val today = PersianDateUtils.fromTimestamp(System.currentTimeMillis())
    var year by remember { mutableStateOf(today.year.toString()) }
    var month by remember { mutableStateOf(today.month.toString()) }
    var day by remember { mutableStateOf(today.day.toString()) }
    var hour by remember { mutableStateOf("9") }
    var minute by remember { mutableStateOf("0") }

    fun applyQuickPick(daysFromNow: Int) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysFromNow)
        val j = PersianDateUtils.fromTimestamp(cal.timeInMillis)
        year = j.year.toString(); month = j.month.toString(); day = j.day.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("یادآوری جدید") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("عنوان") })
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("توضیحات (اختیاری)") })

                Text("تاریخ (شمسی)", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = { applyQuickPick(0) }, label = { Text("امروز") })
                    AssistChip(onClick = { applyQuickPick(1) }, label = { Text("فردا") })
                    AssistChip(onClick = { applyQuickPick(7) }, label = { Text("هفته دیگر") })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("سال") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = month, onValueChange = { month = it }, label = { Text("ماه") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("روز") }, modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = hour, onValueChange = { hour = it }, label = { Text("ساعت") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = minute, onValueChange = { minute = it }, label = { Text("دقیقه") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val ts = PersianDateUtils.jalaliToTimestamp(
                    year.toIntOrNull() ?: today.year,
                    month.toIntOrNull() ?: today.month,
                    day.toIntOrNull() ?: today.day,
                    hour.toIntOrNull() ?: 9,
                    minute.toIntOrNull() ?: 0
                )
                if (title.isNotBlank()) onConfirm(title, note.ifBlank { null }, ts)
            }) { Text("ذخیره") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("انصراف") } }
    )
}
