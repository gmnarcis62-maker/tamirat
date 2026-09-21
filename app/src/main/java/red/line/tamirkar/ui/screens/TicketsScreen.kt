package red.line.tamirkar.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Customer
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.data.Technician
import red.line.tamirkar.data.TicketStatus
import red.line.tamirkar.ui.theme.*
import red.line.tamirkar.util.PersianDateUtils
import red.line.tamirkar.util.SmsHelper

@Composable
fun TicketsScreen(
    tickets: List<RepairTicket>,
    customers: List<Customer>,
    technicians: List<Technician>,
    shopName: String,
    onQueryChange: (String) -> Unit,
    onStatusChange: (RepairTicket, TicketStatus) -> Unit,
    onUpdateTicket: (RepairTicket, String, String, String, String?, Double, Double?) -> Unit,
    onDeleteTicket: (RepairTicket) -> Unit,
    onAssignTechnician: (RepairTicket, Long?) -> Unit,
    onGenerateInvoice: (RepairTicket, String, String) -> Unit,
    onSendSms: (String, String) -> Unit,
    onCaptureSignature: (RepairTicket) -> Unit,
    onPickBeforeAfterPhoto: (onPicked: (Uri) -> Unit) -> Unit,
    onUpdatePhotos: (RepairTicket, String?, String?) -> Unit
) {
    var filter by remember { mutableStateOf<TicketStatus?>(null) }
    var query by remember { mutableStateOf("") }
    var editingTicket by remember { mutableStateOf<RepairTicket?>(null) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("لیست فیش‌های تعمیر", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            placeholder = { Text("جستجو بر اساس برند، مدل یا ایراد...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        LazyColumn {
            item {
                Row(modifier = Modifier.padding(vertical = 8.dp)) {
                    FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text("همه") })
                    Spacer(Modifier.width(8.dp))
                    TicketStatus.entries.forEach { status ->
                        FilterChip(
                            selected = filter == status,
                            onClick = { filter = status },
                            label = { Text(status.label) }
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }

            val filtered = tickets.filter { filter == null || it.status == filter }
            items(filtered) { ticket ->
                val customer = customers.find { it.id == ticket.customerId }
                TicketCard(
                    ticket = ticket,
                    customerName = customer?.fullName ?: "نامشخص",
                    onStatusChange = onStatusChange,
                    onEdit = { editingTicket = ticket }
                )
                Spacer(Modifier.height(8.dp))
            }

            if (filtered.isEmpty()) {
                item {
                    red.line.tamirkar.ui.components.EmptyState(
                        icon = Icons.Filled.Assignment,
                        title = "فیشی یافت نشد",
                        description = "برای شروع، یک دستگاه جدید پذیرش کنید"
                    )
                }
            }
        }
    }

    editingTicket?.let { ticket ->
        val customer = customers.find { it.id == ticket.customerId }
        TicketEditDialog(
            ticket = ticket,
            customer = customer,
            technicians = technicians,
            onDismiss = { editingTicket = null },
            onConfirm = { brand, model, problem, accessories, agreed, final ->
                onUpdateTicket(ticket, brand, model, problem, accessories, agreed, final)
                editingTicket = null
            },
            onDelete = {
                onDeleteTicket(ticket)
                editingTicket = null
            },
            onAssignTechnician = { techId -> onAssignTechnician(ticket, techId) },
            onGenerateInvoice = {
                onGenerateInvoice(ticket, customer?.fullName ?: "مشتری", customer?.phoneNumber ?: "")
            },
            onSendSms = {
                customer?.let {
                    val message = SmsHelper.readyMessage(it.fullName, ticket, shopName.ifBlank { "دستیار تعمیرکار" })
                    onSendSms(it.phoneNumber, message)
                }
            },
            onCaptureSignature = {
                onCaptureSignature(ticket)
                editingTicket = null
            },
            onPickBeforePhoto = { onPickBeforeAfterPhoto { uri -> onUpdatePhotos(ticket, uri.toString(), null) } },
            onPickAfterPhoto = { onPickBeforeAfterPhoto { uri -> onUpdatePhotos(ticket, null, uri.toString()) } }
        )
    }
}

@Composable
private fun TicketCard(
    ticket: RepairTicket,
    customerName: String,
    onStatusChange: (RepairTicket, TicketStatus) -> Unit,
    onEdit: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = when (ticket.status) {
        TicketStatus.PENDING -> StatusPending
        TicketStatus.IN_PROGRESS -> StatusInProgress
        TicketStatus.READY -> StatusReady
        TicketStatus.DELIVERED -> StatusDelivered
        TicketStatus.CANCELLED -> StatusCancelled
    }

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text("${ticket.deviceBrand} ${ticket.deviceModel}", style = MaterialTheme.typography.titleMedium)
                    Text(customerName, style = MaterialTheme.typography.bodyMedium)
                    Text(PersianDateUtils.formatDate(ticket.receivedAt), style = MaterialTheme.typography.labelSmall)
                }
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "ویرایش") }
                    AssistChip(onClick = { expanded = true }, label = { Text(ticket.status.label) }, colors = AssistChipDefaults.assistChipColors(labelColor = statusColor))
                }
            }
            Text(ticket.problemDescription, style = MaterialTheme.typography.bodyMedium)
            Text("قیمت توافقی: ${PersianDateUtils.formatToman(ticket.agreedPrice)}", style = MaterialTheme.typography.bodyMedium)

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                TicketStatus.entries.forEach { status ->
                    DropdownMenuItem(text = { Text(status.label) }, onClick = {
                        onStatusChange(ticket, status)
                        expanded = false
                    })
                }
            }
        }
    }
}

@Composable
private fun TicketEditDialog(
    ticket: RepairTicket,
    customer: Customer?,
    technicians: List<Technician>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String?, Double, Double?) -> Unit,
    onDelete: () -> Unit,
    onAssignTechnician: (Long?) -> Unit,
    onGenerateInvoice: () -> Unit,
    onSendSms: () -> Unit,
    onCaptureSignature: () -> Unit,
    onPickBeforePhoto: () -> Unit,
    onPickAfterPhoto: () -> Unit
) {
    var brand by remember { mutableStateOf(ticket.deviceBrand) }
    var model by remember { mutableStateOf(ticket.deviceModel) }
    var problem by remember { mutableStateOf(ticket.problemDescription) }
    var accessories by remember { mutableStateOf(ticket.accessories ?: "") }
    var agreed by remember { mutableStateOf(ticket.agreedPrice.toString()) }
    var final by remember { mutableStateOf(ticket.finalPrice?.toString() ?: "") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var techMenuExpanded by remember { mutableStateOf(false) }
    val selectedTech = technicians.find { it.id == ticket.technicianId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ویرایش فیش تعمیر") },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("برند") })
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("مدل") })
                OutlinedTextField(value = problem, onValueChange = { problem = it }, label = { Text("شرح ایراد") }, minLines = 2)
                OutlinedTextField(value = accessories, onValueChange = { accessories = it }, label = { Text("لوازم همراه") })
                OutlinedTextField(value = agreed, onValueChange = { agreed = it }, label = { Text("قیمت توافقی") })
                OutlinedTextField(value = final, onValueChange = { final = it }, label = { Text("قیمت نهایی (اختیاری)") })

                if (technicians.isNotEmpty()) {
                    ExposedDropdownMenuBox(expanded = techMenuExpanded, onExpandedChange = { techMenuExpanded = it }) {
                        OutlinedTextField(
                            value = selectedTech?.name ?: "تعیین نشده",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("تکنسین مسئول") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = techMenuExpanded, onDismissRequest = { techMenuExpanded = false }) {
                            DropdownMenuItem(text = { Text("تعیین نشده") }, onClick = { onAssignTechnician(null); techMenuExpanded = false })
                            technicians.forEach { tech ->
                                DropdownMenuItem(text = { Text(tech.name) }, onClick = { onAssignTechnician(tech.id); techMenuExpanded = false })
                            }
                        }
                    }
                }

                Divider()

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onGenerateInvoice) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("فاکتور PDF")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onSendSms, enabled = customer != null) {
                        Icon(Icons.Filled.Sms, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("پیامک آماده شدن")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onCaptureSignature) {
                        Icon(Icons.Filled.Draw, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (ticket.signatureUri != null) "مشاهده/تغییر امضا" else "ثبت امضای تحویل")
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onPickBeforePhoto) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (ticket.beforePhotoUri != null) "عکس قبل ثبت شد ✓" else "عکس قبل از تعمیر")
                    }
                }
                ticket.beforePhotoUri?.let { uri ->
                    coil.compose.AsyncImage(
                        model = uri, contentDescription = "عکس قبل از تعمیر",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextButton(onClick = onPickAfterPhoto) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text(if (ticket.afterPhotoUri != null) "عکس بعد ثبت شد ✓" else "عکس بعد از تعمیر")
                    }
                }
                ticket.afterPhotoUri?.let { uri ->
                    coil.compose.AsyncImage(
                        model = uri, contentDescription = "عکس بعد از تعمیر",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                }
                ticket.signatureUri?.let { uri ->
                    Text("امضای ثبت‌شده:", style = MaterialTheme.typography.labelSmall)
                    coil.compose.AsyncImage(
                        model = uri, contentDescription = "امضا",
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                        modifier = Modifier.fillMaxWidth().height(100.dp).background(androidx.compose.ui.graphics.Color.White)
                    )
                }

                TextButton(onClick = { showDeleteConfirm = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.width(4.dp))
                    Text("حذف این فیش", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    brand, model, problem, accessories.ifBlank { null },
                    agreed.toDoubleOrNull() ?: ticket.agreedPrice,
                    final.toDoubleOrNull()
                )
            }) { Text("ذخیره") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف فیش") },
            text = { Text("آیا از حذف این فیش تعمیر مطمئن هستید؟") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    onDelete()
                }) { Text("حذف", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("انصراف") }
            }
        )
    }
}
