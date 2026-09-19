package red.line.tamirkar.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Destination("dashboard", "داشبورد", Icons.Filled.Dashboard)
    data object Customers : Destination("customers", "مشتریان", Icons.Filled.People)
    data object Tickets : Destination("tickets", "فیش‌ها", Icons.Filled.Build)
    data object Inventory : Destination("inventory", "انبار", Icons.Filled.Inventory)
    data object Settings : Destination("settings", "تنظیمات", Icons.Filled.Settings)
    data object NewTicket : Destination("new_ticket", "پذیرش جدید", Icons.Filled.Add)
    data object Troubleshooting : Destination("troubleshooting", "عیب‌یابی", Icons.Filled.Search)
    data object Accounting : Destination("accounting", "حسابداری", Icons.Filled.Payments)
    data object Technicians : Destination("technicians", "تکنسین‌ها", Icons.Filled.Group)
    data object Reminders : Destination("reminders", "یادآوری‌ها", Icons.Filled.Event)
    data object PrivacyPolicy : Destination("privacy_policy", "حریم خصوصی", Icons.Filled.PrivacyTip)
    data object TermsOfService : Destination("terms_of_service", "شرایط استفاده", Icons.Filled.Gavel)
    data object Signature : Destination("signature/{ticketId}", "امضا", Icons.Filled.Draw) {
        fun buildRoute(ticketId: Long) = "signature/$ticketId"
    }

    companion object {
        val bottomBarItems = listOf(Dashboard, Customers, Tickets, Inventory, Settings)
    }
}
