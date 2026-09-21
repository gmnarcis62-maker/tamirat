package red.line.tamirkar.ui.navigation

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import red.line.tamirkar.data.RepairTicket
import red.line.tamirkar.ui.screens.*
import red.line.tamirkar.util.SmsHelper
import red.line.tamirkar.viewmodel.MainViewModel

@Composable
fun TamirkarNavGraph(
    viewModel: MainViewModel,
    onUpgradeClick: () -> Unit,
    onRateOnMyketClick: () -> Unit,
    onSupportClick: () -> Unit,
    onPickImage: (onPicked: (Uri) -> Unit) -> Unit,
    onGeneratePdfInvoice: (ticket: RepairTicket, customerName: String, customerPhone: String) -> Unit,
    onExportCsv: (type: String) -> Unit,
    onBackupExport: () -> Unit,
    onBackupRestore: () -> Unit,
    onSendReadySms: (phone: String, message: String) -> Unit,
    onScheduleReminder: (red.line.tamirkar.data.Reminder) -> Unit
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val isVip by viewModel.isVip.collectAsState()
    val customers by viewModel.customers.collectAsState()
    val tickets by viewModel.tickets.collectAsState()
    val activeCount by viewModel.activeTicketCount.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val freeTierLimitReached by viewModel.freeTierLimitReached.collectAsState()
    val troubleshootingGuides by viewModel.troubleshootingGuides.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val todayIncome by viewModel.todayIncome.collectAsState()
    val todayExpense by viewModel.todayExpense.collectAsState()
    val monthIncome by viewModel.monthIncome.collectAsState()
    val monthExpense by viewModel.monthExpense.collectAsState()
    val shopProfile by viewModel.shopProfile.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val autoSmsEnabled by viewModel.autoSmsEnabled.collectAsState()
    val technicians by viewModel.technicians.collectAsState()
    val reminders by viewModel.reminders.collectAsState()

    // نگه‌داشتن فیشی که در حال ثبت امضا برای آن هستیم
    var signatureTargetTicketId by remember { mutableStateOf<Long?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val isBusy by viewModel.isBusy.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.messages.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // نوار پایین فقط در صفحات اصلی نمایش داده می‌شود
            if (Destination.bottomBarItems.any { it.route == currentRoute }) {
                NavigationBar {
                    Destination.bottomBarItems.forEach { dest ->
                        NavigationBarItem(
                            selected = currentRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
        NavHost(
            navController = navController,
            startDestination = Destination.Dashboard.route,
            modifier = Modifier
        ) {
            composable(Destination.Dashboard.route) {
                DashboardScreen(
                    activeTicketCount = activeCount,
                    todayIncome = todayIncome,
                    lowStockCount = inventory.count { it.quantity <= it.lowStockThreshold },
                    isVip = isVip,
                    onNewTicketClick = { navController.navigate(Destination.NewTicket.route) },
                    onTroubleshootingClick = { navController.navigate(Destination.Troubleshooting.route) },
                    onAccountingClick = { navController.navigate(Destination.Accounting.route) },
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Customers.route) {
                CustomersScreen(
                    customers = customers,
                    allTickets = tickets,
                    onQueryChange = { viewModel.setCustomerQuery(it) },
                    onAddCustomer = { name, phone, address, note -> viewModel.addCustomer(name, phone, address, note) },
                    onUpdateCustomer = { c, name, phone, address, note -> viewModel.updateCustomer(c, name, phone, address, note) },
                    onDeleteCustomer = { viewModel.deleteCustomer(it) }
                )
            }
            composable(Destination.NewTicket.route) {
                NewTicketScreen(
                    customers = customers,
                    inventoryParts = inventory,
                    freeTierLimitReached = freeTierLimitReached,
                    onSubmit = { customerId, brand, model, problem, accessories, price, expectedDelivery, beforePhoto ->
                        viewModel.addTicket(customerId, brand, model, problem, accessories, price, expectedDelivery, beforePhoto)
                        navController.popBackStack()
                    },
                    onPickImage = onPickImage,
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Tickets.route) {
                TicketsScreen(
                    tickets = tickets,
                    customers = customers,
                    technicians = technicians,
                    shopName = shopProfile.shopName,
                    onQueryChange = { viewModel.setTicketQuery(it) },
                    onStatusChange = { ticket, status -> viewModel.updateTicketStatus(ticket, status) },
                    onUpdateTicket = { ticket, brand, model, problem, accessories, agreed, final ->
                        viewModel.updateTicketDetails(ticket, brand, model, problem, accessories, agreed, final)
                    },
                    onDeleteTicket = { viewModel.deleteTicket(it) },
                    onAssignTechnician = { ticket, techId -> viewModel.assignTechnician(ticket, techId) },
                    onGenerateInvoice = { ticket, customerName, customerPhone -> onGeneratePdfInvoice(ticket, customerName, customerPhone) },
                    onSendSms = { phone, message -> onSendReadySms(phone, message) },
                    onCaptureSignature = { ticket ->
                        signatureTargetTicketId = ticket.id
                        navController.navigate(Destination.Signature.buildRoute(ticket.id))
                    },
                    onPickBeforeAfterPhoto = onPickImage,
                    onUpdatePhotos = { ticket, before, after -> viewModel.updateTicketPhotos(ticket, before, after, null) }
                )
            }
            composable(Destination.Inventory.route) {
                InventoryScreen(
                    isVip = isVip,
                    parts = inventory,
                    onAddPart = { n, m, q, b, s -> viewModel.addPart(n, m, q, b, s) },
                    onUpdatePart = { part, n, m, q, b, s -> viewModel.updatePart(part, n, m, q, b, s) },
                    onDeletePart = { viewModel.deletePart(it) },
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Troubleshooting.route) {
                TroubleshootingScreen(
                    isVip = isVip,
                    guides = troubleshootingGuides,
                    onQueryChange = { viewModel.setTroubleshootingQuery(it) },
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Accounting.route) {
                AccountingScreen(
                    isVip = isVip,
                    transactions = transactions,
                    todayIncome = todayIncome,
                    todayExpense = todayExpense,
                    monthIncome = monthIncome,
                    monthExpense = monthExpense,
                    onAddTransaction = { type, category, amount, note -> viewModel.addTransaction(type, category, amount, note) },
                    onDeleteTransaction = { viewModel.deleteTransaction(it) },
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Settings.route) {
                SettingsScreen(
                    isVip = isVip,
                    shopProfile = shopProfile,
                    themeMode = themeMode,
                    autoSmsEnabled = autoSmsEnabled,
                    onUpgradeClick = onUpgradeClick,
                    onRateOnMyketClick = onRateOnMyketClick,
                    onSupportClick = onSupportClick,
                    onSaveShopProfile = { viewModel.saveShopProfile(it) },
                    onPickLogo = onPickImage,
                    onThemeModeChange = { viewModel.setThemeMode(it) },
                    onAutoSmsChange = { viewModel.setAutoSmsEnabled(it) },
                    onExportCsv = onExportCsv,
                    onBackupExport = onBackupExport,
                    onBackupRestore = onBackupRestore,
                    onOpenTechnicians = { navController.navigate(Destination.Technicians.route) },
                    onOpenReminders = { navController.navigate(Destination.Reminders.route) },
                    onOpenPrivacyPolicy = { navController.navigate(Destination.PrivacyPolicy.route) },
                    onOpenTermsOfService = { navController.navigate(Destination.TermsOfService.route) }
                )
            }
            composable(Destination.Technicians.route) {
                TechniciansScreen(
                    isVip = isVip,
                    technicians = technicians,
                    onBack = { navController.popBackStack() },
                    onAdd = { name, pin -> viewModel.addTechnician(name, pin) },
                    onUpdate = { tech, name, pin, active -> viewModel.updateTechnician(tech, name, pin, active) },
                    onDelete = { viewModel.deleteTechnician(it) },
                    onUpgradeClick = onUpgradeClick
                )
            }
            composable(Destination.Reminders.route) {
                RemindersScreen(
                    reminders = reminders,
                    onBack = { navController.popBackStack() },
                    onAdd = { title, note, dateTime ->
                        viewModel.addReminder(title, note, dateTime, null) { reminder ->
                            onScheduleReminder(reminder)
                        }
                    },
                    onComplete = { viewModel.completeReminder(it) },
                    onDelete = { viewModel.deleteReminder(it) }
                )
            }
            composable(Destination.PrivacyPolicy.route) {
                PrivacyPolicyScreen(onBack = { navController.popBackStack() })
            }
            composable(Destination.TermsOfService.route) {
                TermsOfServiceScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Destination.Signature.route,
                arguments = listOf(navArgument("ticketId") { type = NavType.LongType })
            ) { backStackEntry ->
                val ticketId = backStackEntry.arguments?.getLong("ticketId") ?: 0L
                SignatureScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { uri ->
                        val ticket = tickets.find { it.id == ticketId }
                        ticket?.let { viewModel.updateTicketPhotos(it, null, null, uri.toString()) }
                        navController.popBackStack()
                    }
                )
            }
        }

        if (isBusy) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        }
    }
}