package red.line.tamirkar.data

import kotlinx.coroutines.flow.Flow

/**
 * لایه‌ی Repository — واسط بین ViewModel ها و دیتابیس
 * حد مجاز فیش فعال در نسخه رایگان (طبق طرح محصول)
 */
class TamirkarRepository(
    private val customerDao: CustomerDao,
    private val ticketDao: RepairTicketDao,
    private val inventoryDao: InventoryDao,
    private val troubleshootingDao: TroubleshootingDao,
    private val transactionDao: TransactionDao,
    private val technicianDao: TechnicianDao,
    private val reminderDao: ReminderDao
) {
    companion object {
        const val FREE_TIER_MAX_ACTIVE_TICKETS = 10
    }

    // مشتریان
    fun getCustomers(): Flow<List<Customer>> = customerDao.getAll()
    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.search(query)
    suspend fun addCustomer(customer: Customer): Long = customerDao.insert(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.update(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.delete(customer)

    // فیش‌های تعمیر
    fun getTickets(): Flow<List<RepairTicket>> = ticketDao.getAll()
    fun searchTickets(query: String): Flow<List<RepairTicket>> = ticketDao.search(query)
    fun getTicketsByStatus(status: TicketStatus): Flow<List<RepairTicket>> = ticketDao.getByStatus(status)
    fun getActiveTicketCount(): Flow<Int> = ticketDao.getActiveCount()
    suspend fun addTicket(ticket: RepairTicket): Long = ticketDao.insert(ticket)
    suspend fun updateTicket(ticket: RepairTicket) = ticketDao.update(ticket)
    suspend fun deleteTicket(ticket: RepairTicket) = ticketDao.delete(ticket)

    // انبار قطعات (فقط VIP)
    fun getInventory(): Flow<List<InventoryPart>> = inventoryDao.getAll()
    fun getLowStockParts(): Flow<List<InventoryPart>> = inventoryDao.getLowStock()
    suspend fun addPart(part: InventoryPart): Long = inventoryDao.insert(part)
    suspend fun updatePart(part: InventoryPart) = inventoryDao.update(part)
    suspend fun deletePart(part: InventoryPart) = inventoryDao.delete(part)

    // پایگاه‌داده عیب‌یابی گوشی
    fun getTroubleshootingGuides(): Flow<List<TroubleshootingGuide>> = troubleshootingDao.getAll()
    fun searchTroubleshootingGuides(query: String): Flow<List<TroubleshootingGuide>> = troubleshootingDao.search(query)
    fun getGuidesByCategory(category: TroubleshootingCategory): Flow<List<TroubleshootingGuide>> = troubleshootingDao.getByCategory(category)

    // حسابداری و صندوق
    fun getTransactions(): Flow<List<Transaction>> = transactionDao.getAll()
    fun getTransactionsByRange(start: Long, end: Long): Flow<List<Transaction>> = transactionDao.getByDateRange(start, end)
    fun getTotalByTypeAndRange(type: TransactionType, start: Long, end: Long): Flow<Double> =
        transactionDao.getTotalByTypeAndRange(type, start, end)
    suspend fun addTransaction(transaction: Transaction): Long = transactionDao.insert(transaction)
    suspend fun deleteTransaction(transaction: Transaction) = transactionDao.delete(transaction)
    suspend fun markTicketIncomeRecorded(ticket: RepairTicket) = ticketDao.update(ticket.copy(incomeRecorded = true))

    // تکنسین‌ها (چند کاربره)
    fun getTechnicians(): Flow<List<Technician>> = technicianDao.getAll()
    fun getActiveTechnicians(): Flow<List<Technician>> = technicianDao.getActive()
    suspend fun addTechnician(technician: Technician): Long = technicianDao.insert(technician)
    suspend fun updateTechnician(technician: Technician) = technicianDao.update(technician)
    suspend fun deleteTechnician(technician: Technician) = technicianDao.delete(technician)

    // یادآوری‌ها و تقویم
    fun getReminders(): Flow<List<Reminder>> = reminderDao.getAll()
    fun getUpcomingReminders(): Flow<List<Reminder>> = reminderDao.getUpcoming()
    suspend fun addReminder(reminder: Reminder): Long = reminderDao.insert(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.update(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)

    // پشتیبان‌گیری محلی (Backup/Restore)
    suspend fun restoreAll(
        customers: List<Customer>,
        tickets: List<RepairTicket>,
        parts: List<InventoryPart>,
        transactions: List<Transaction>,
        technicians: List<Technician>,
        reminders: List<Reminder>
    ) {
        customerDao.deleteAll()
        ticketDao.deleteAll()
        inventoryDao.deleteAll()
        transactionDao.deleteAll()
        technicianDao.deleteAllExceptOwner()
        reminderDao.deleteAll()

        customerDao.insertAll(customers)
        ticketDao.insertAll(tickets)
        inventoryDao.insertAll(parts)
        transactionDao.insertAll(transactions)
        // مدیر تعمیرگاه (isOwner=true) همیشه حفظ می‌شود؛ فقط تکنسین‌های عادی از بک‌آپ بازیابی می‌شوند
        technicianDao.insertAll(technicians.filter { !it.isOwner })
        reminderDao.insertAll(reminders)
    }
}
