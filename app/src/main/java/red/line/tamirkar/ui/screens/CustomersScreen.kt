package red.line.tamirkar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import red.line.tamirkar.data.Customer
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.util.PersianDateUtils

@Composable
fun CustomersScreen(
    customers: List<Customer>,
    allTickets: List<RepairTicket>,
    onQueryChange: (String) -> Unit,
    onAddCustomer: (String, String, String?, String?) -> Unit,
    onUpdateCustomer: (Customer, String, String, String?, String?) -> Unit,
    onDeleteCustomer: (Customer) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCustomer by remember { mutableStateOf<Customer?>(null) }
    var detailCustomer by remember { mutableStateOf<Customer?>(null) }
    var query by remember { mutableStateOf("") }

    if (detailCustomer != null) {
        CustomerDetailScreen(
            customer = detailCustomer!!,
            tickets = allTickets.filter { it.customerId == detailCustomer!!.id },
            onBack = { detailCustomer = null },
            onEdit = { editingCustomer = detailCustomer },
            onDelete = {
                onDeleteCustomer(detailCustomer!!)
                detailCustomer = null
            }
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "افزودن مشتری")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onQueryChange(it)
                    },
                    placeholder = { Text("جستجو بر اساس نام یا شماره تماس...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (customers.isEmpty()) {
                        item {
                            red.line.tamirkar.ui.components.EmptyState(
                                icon = Icons.Filled.People,
                                title = "هنوز مشتری‌ای ثبت نشده",
                                description = "با دکمه‌ی + یک مشتری جدید اضافه کنید"
                            )
                        }
                    }
                    items(customers) { customer ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { detailCustomer = customer }
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(customer.fullName, style = MaterialTheme.typography.titleMedium)
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(customer.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                                }
                                customer.note?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CustomerFormDialog(
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, address, note ->
                onAddCustomer(name, phone, address, note)
                showAddDialog = false
            }
        )
    }

    editingCustomer?.let { c ->
        CustomerFormDialog(
            initial = c,
            onDismiss = { editingCustomer = null },
            onConfirm = { name, phone, address, note ->
                onUpdateCustomer(c, name, phone, address, note)
                editingCustomer = null
            }
        )
    }
}

@Composable
private fun CustomerFormDialog(
    initial: Customer?,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf(initial?.fullName ?: "") }
    var phone by remember { mutableStateOf(initial?.phoneNumber ?: "") }
    var address by remember { mutableStateOf(initial?.address ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "مشتری جدید" else "ویرایش مشتری") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام و نام خانوادگی") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("شماره تماس") })
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("آدرس (اختیاری)") })
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name, phone, address.ifBlank { null }, initial?.note) }) {
                Text("ذخیره")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف") }
        }
    )
}

@Composable
private fun CustomerDetailScreen(
    customer: Customer,
    tickets: List<RepairTicket>,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer.fullName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) { Icon(Icons.Filled.Edit, contentDescription = "ویرایش") }
                    IconButton(onClick = { showDeleteConfirm = true }) { Icon(Icons.Filled.Delete, contentDescription = "حذف") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("شماره تماس: ${customer.phoneNumber}")
                    customer.address?.let { Text("آدرس: $it") }
                    Text("مشتری از: ${PersianDateUtils.formatDateLong(customer.createdAt)}")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("تاریخچه تعمیرات (${PersianDateUtils.toPersianNumber(tickets.size)} فیش)", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tickets) { ticket ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("${ticket.deviceBrand} ${ticket.deviceModel} — ${ticket.status.label}", style = MaterialTheme.typography.bodyLarge)
                            Text(ticket.problemDescription, style = MaterialTheme.typography.bodyMedium)
                            Text(PersianDateUtils.formatDate(ticket.receivedAt), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (tickets.isEmpty()) {
                    item { Text("این مشتری هنوز فیشی ندارد", style = MaterialTheme.typography.bodyMedium) }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("حذف مشتری") },
            text = { Text("آیا از حذف «${customer.fullName}» مطمئن هستید؟ این عملیات غیرقابل بازگشت است.") },
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
