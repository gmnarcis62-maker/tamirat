package red.line.tamirkar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import red.line.tamirkar.data.*

class MainViewModel(
    private val repository: TamirkarRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    // ===== پیام‌های بازخورد به کاربر (Snackbar) =====
    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    private fun emitMessage(text: String) {
        viewModelScope.launch { _messages.emit(text) }
    }

    /** برای اعلام پیام از بیرون ViewModel (مثل MainActivity بعد از تولید PDF/CSV/بک‌آپ) */
    fun notify(text: String) = emitMessage(text)

    // نشانگر بارگذاری برای عملیات طولانی (تولید PDF/CSV/بک‌آپ) که در MainActivity اجرا می‌شوند
    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy
    fun setBusy(value: Boolean) { _isBusy.value = value }

    // وضعیت اشتراک VIP کاربر — پایدار با DataStore، به BillingManager هم وصل می‌شود
    val isVip: StateFlow<Boolean> =
        preferences.isVip.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setVipStatus(value: Boolean) {
        viewModelScope.launch { preferences.setIsVip(value) }
    }

    // تم برنامه (روشن/تاریک/خودکار)
    val themeMode: StateFlow<ThemeMode> =
        preferences.themeMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    val onboardingDone: StateFlow<Boolean> =
        preferences.onboardingDone.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun completeOnboarding() {
        viewModelScope.launch { preferences.setOnboardingDone(true) }
    }

    // پروفایل فروشگاه (برای فاکتور و تنظیمات)
    val shopProfile: StateFlow<ShopProfile> =
        preferences.shopProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ShopProfile())

    fun saveShopProfile(profile: ShopProfile) {
        viewModelScope.launch {
            preferences.saveShopProfile(profile)
            emitMessage("پروفایل فروشگاه ذخیره شد")
        }
    }

    // ارسال خودکار پیامک هنگام آماده شدن دستگاه
    val autoSmsEnabled: StateFlow<Boolean> =
        preferences.autoSmsEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setAutoSmsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferences.setAutoSmsEnabled(enabled) }
    }

    val customers: StateFlow<List<Customer>> by lazy {
        customerQuery
            .debounce(250)
            .flatMapLatest { q -> if (q.isBlank()) repository.getCustomers() else repository.searchCustomers(q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    private val customerQuery = MutableStateFlow("")
    fun setCustomerQuery(query: String) {
        customerQuery.value = query
    }

    val tickets: StateFlow<List<RepairTicket>> by lazy {
        ticketQuery
            .debounce(250)
            .flatMapLatest { q -> if (q.isBlank()) repository.getTickets() else repository.searchTickets(q) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    private val ticketQuery = MutableStateFlow("")
    fun setTicketQuery(query: String) {
        ticketQuery.value = query
    }

    val activeTicketCount: StateFlow<Int> =
        repository.getActiveTicketCount().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val inventory: StateFlow<List<InventoryPart>> =
        repository.getInventory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** true یعنی کاربر رایگان به سقف مجاز فیش فعال رسیده و باید VIP بخرد */
    val freeTierLimitReached: StateFlow<Boolean> =
        combine(activeTicketCount, isVip) { count, vip ->
            !vip && count >= TamirkarRepository.FREE_TIER_MAX_ACTIVE_TICKETS
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun addCustomer(fullName: String, phone: String, address: String?, note: String?) {
        if (!red.line.tamirkar.util.Validators.isRequired(fullName)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (!red.line.tamirkar.util.Validators.isValidPhone(phone)) {
            emitMessage(red.line.tamirkar.util.Validators.PHONE_ERROR); return
        }
        viewModelScope.launch {
            repository.addCustomer(Customer(fullName = fullName, phoneNumber = phone, address = address, note = note))
            emitMessage("مشتری با موفقیت ثبت شد")
        }
    }

    fun updateCustomer(customer: Customer, fullName: String, phone: String, address: String?, note: String?) {
        if (!red.line.tamirkar.util.Validators.isRequired(fullName)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (!red.line.tamirkar.util.Validators.isValidPhone(phone)) {
            emitMessage(red.line.tamirkar.util.Validators.PHONE_ERROR); return
        }
        viewModelScope.launch {
            repository.updateCustomer(customer.copy(fullName = fullName, phoneNumber = phone, address = address, note = note))
            emitMessage("مشتری بروزرسانی شد")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            emitMessage("مشتری حذف شد")
        }
    }

    /** تاریخچه‌ی فیش‌های یک مشتری خاص (برای صفحه جزئیات مشتری) */
    fun ticketsForCustomer(customerId: Long): List<RepairTicket> =
        tickets.value.filter { it.customerId == customerId }

    fun addTicket(
        customerId: Long,
        brand: String,
        model: String,
        problem: String,
        accessories: String?,
        agreedPrice: Double,
        expectedDeliveryDate: Long? = null,
        beforePhotoUri: String? = null
    ) {
        if (!red.line.tamirkar.util.Validators.isRequired(brand) || !red.line.tamirkar.util.Validators.isRequired(model) || !red.line.tamirkar.util.Validators.isRequired(problem)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (!red.line.tamirkar.util.Validators.isNonNegativePrice(agreedPrice)) {
            emitMessage(red.line.tamirkar.util.Validators.PRICE_ERROR); return
        }
        viewModelScope.launch {
            repository.addTicket(
                RepairTicket(
                    customerId = customerId,
                    deviceBrand = brand,
                    deviceModel = model,
                    problemDescription = problem,
                    accessories = accessories,
                    agreedPrice = agreedPrice,
                    expectedDeliveryDate = expectedDeliveryDate,
                    beforePhotoUri = beforePhotoUri
                )
            )
            emitMessage("فیش تعمیر با موفقیت ثبت شد")
        }
    }

    fun updateTicketDetails(
        ticket: RepairTicket,
        brand: String,
        model: String,
        problem: String,
        accessories: String?,
        agreedPrice: Double,
        finalPrice: Double?
    ) {
        if (!red.line.tamirkar.util.Validators.isNonNegativePrice(agreedPrice) || (finalPrice != null && !red.line.tamirkar.util.Validators.isNonNegativePrice(finalPrice))) {
            emitMessage(red.line.tamirkar.util.Validators.PRICE_ERROR); return
        }
        viewModelScope.launch {
            repository.updateTicket(
                ticket.copy(
                    deviceBrand = brand,
                    deviceModel = model,
                    problemDescription = problem,
                    accessories = accessories,
                    agreedPrice = agreedPrice,
                    finalPrice = finalPrice
                )
            )
            emitMessage("فیش بروزرسانی شد")
        }
    }

    fun deleteTicket(ticket: RepairTicket) {
        viewModelScope.launch {
            repository.deleteTicket(ticket)
            emitMessage("فیش حذف شد")
        }
    }

    fun updateTicketStatus(ticket: RepairTicket, newStatus: TicketStatus) {
        viewModelScope.launch {
            val deliveredAt = if (newStatus == TicketStatus.DELIVERED) System.currentTimeMillis() else ticket.deliveredAt
            val updated = ticket.copy(status = newStatus, deliveredAt = deliveredAt)
            repository.updateTicket(updated)

            // ثبت خودکار درآمد در حسابداری هنگام تحویل دستگاه (فقط یک‌بار برای هر فیش)
            if (newStatus == TicketStatus.DELIVERED && !ticket.incomeRecorded) {
                val amount = ticket.finalPrice ?: ticket.agreedPrice
                repository.addTransaction(
                    Transaction(
                        type = TransactionType.INCOME,
                        category = TransactionCategory.REPAIR_INCOME,
                        amount = amount,
                        note = "تحویل ${ticket.deviceBrand} ${ticket.deviceModel}",
                        relatedTicketId = ticket.id
                    )
                )
                repository.markTicketIncomeRecorded(ticket)
            }

            // رویداد آماده شدن دستگاه (برای ارسال پیامک خودکار در صورت فعال بودن)
            if (newStatus == TicketStatus.READY) {
                _ticketReadyEvent.emit(updated)
            }
        }
    }

    private val _ticketReadyEvent = MutableSharedFlow<RepairTicket>()
    val ticketReadyEvent: SharedFlow<RepairTicket> = _ticketReadyEvent

    fun updateTicketPhotos(ticket: RepairTicket, beforeUri: String?, afterUri: String?, signatureUri: String?) {
        viewModelScope.launch {
            repository.updateTicket(
                ticket.copy(
                    beforePhotoUri = beforeUri ?: ticket.beforePhotoUri,
                    afterPhotoUri = afterUri ?: ticket.afterPhotoUri,
                    signatureUri = signatureUri ?: ticket.signatureUri
                )
            )
        }
    }

    fun assignTechnician(ticket: RepairTicket, technicianId: Long?) {
        viewModelScope.launch {
            repository.updateTicket(ticket.copy(technicianId = technicianId))
        }
    }

    fun setExpectedDeliveryDate(ticket: RepairTicket, dateMillis: Long?) {
        viewModelScope.launch {
            repository.updateTicket(ticket.copy(expectedDeliveryDate = dateMillis))
        }
    }

    fun addPart(name: String, models: String?, qty: Int, buy: Double, sell: Double) {
        if (!red.line.tamirkar.util.Validators.isRequired(name)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (!red.line.tamirkar.util.Validators.isNonNegativePrice(buy) || !red.line.tamirkar.util.Validators.isNonNegativePrice(sell) || qty < 0) {
            emitMessage(red.line.tamirkar.util.Validators.PRICE_ERROR); return
        }
        viewModelScope.launch {
            repository.addPart(InventoryPart(name = name, compatibleModels = models, quantity = qty, purchasePrice = buy, sellPrice = sell))
            emitMessage("قطعه به انبار اضافه شد")
        }
    }

    fun updatePart(part: InventoryPart, name: String, models: String?, qty: Int, buy: Double, sell: Double) {
        if (!red.line.tamirkar.util.Validators.isNonNegativePrice(buy) || !red.line.tamirkar.util.Validators.isNonNegativePrice(sell) || qty < 0) {
            emitMessage(red.line.tamirkar.util.Validators.PRICE_ERROR); return
        }
        viewModelScope.launch {
            repository.updatePart(part.copy(name = name, compatibleModels = models, quantity = qty, purchasePrice = buy, sellPrice = sell))
            emitMessage("قطعه بروزرسانی شد")
        }
    }

    fun deletePart(part: InventoryPart) {
        viewModelScope.launch {
            repository.deletePart(part)
            emitMessage("قطعه از انبار حذف شد")
        }
    }

    // پایگاه‌داده عیب‌یابی گوشی
    private val troubleshootingQuery = MutableStateFlow("")

    fun setTroubleshootingQuery(query: String) {
        troubleshootingQuery.value = query
    }

    val troubleshootingGuides: StateFlow<List<TroubleshootingGuide>> =
        troubleshootingQuery
            .debounce(250)
            .flatMapLatest { query ->
                if (query.isBlank()) repository.getTroubleshootingGuides()
                else repository.searchTroubleshootingGuides(query)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ===================== حسابداری و صندوق =====================

    val transactions: StateFlow<List<Transaction>> =
        repository.getTransactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val calendar get() = java.util.Calendar.getInstance()

    private fun startOfToday(): Long = calendar.apply {
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun startOfMonth(): Long = calendar.apply {
        set(java.util.Calendar.DAY_OF_MONTH, 1)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun now(): Long = System.currentTimeMillis()

    val todayIncome: StateFlow<Double> =
        repository.getTotalByTypeAndRange(TransactionType.INCOME, startOfToday(), now())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayExpense: StateFlow<Double> =
        repository.getTotalByTypeAndRange(TransactionType.EXPENSE, startOfToday(), now())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthIncome: StateFlow<Double> =
        repository.getTotalByTypeAndRange(TransactionType.INCOME, startOfMonth(), now())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthExpense: StateFlow<Double> =
        repository.getTotalByTypeAndRange(TransactionType.EXPENSE, startOfMonth(), now())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addTransaction(type: TransactionType, category: TransactionCategory, amount: Double, note: String?) {
        if (!red.line.tamirkar.util.Validators.isNonNegativePrice(amount) || amount == 0.0) {
            emitMessage("مبلغ باید بزرگ‌تر از صفر باشد"); return
        }
        viewModelScope.launch {
            repository.addTransaction(Transaction(type = type, category = category, amount = amount, note = note))
            emitMessage("تراکنش ثبت شد")
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            emitMessage("تراکنش حذف شد")
        }
    }

    // ===================== تکنسین‌ها (چند کاربره) =====================

    val technicians: StateFlow<List<Technician>> =
        repository.getTechnicians().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeTechnicianId = MutableStateFlow<Long?>(null)
    val activeTechnicianId: StateFlow<Long?> = _activeTechnicianId

    fun switchActiveTechnician(id: Long?) {
        _activeTechnicianId.value = id
    }

    fun addTechnician(name: String, pin: String) {
        if (!red.line.tamirkar.util.Validators.isRequired(name)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (!red.line.tamirkar.util.Validators.isValidPin(pin)) {
            emitMessage(red.line.tamirkar.util.Validators.PIN_ERROR); return
        }
        viewModelScope.launch {
            repository.addTechnician(Technician(name = name, pinCode = pin, isOwner = false))
            emitMessage("تکنسین اضافه شد")
        }
    }

    fun updateTechnician(technician: Technician, name: String, pin: String, active: Boolean) {
        if (!red.line.tamirkar.util.Validators.isValidPin(pin)) {
            emitMessage(red.line.tamirkar.util.Validators.PIN_ERROR); return
        }
        viewModelScope.launch {
            repository.updateTechnician(technician.copy(name = name, pinCode = pin, active = active))
            emitMessage("اطلاعات تکنسین بروزرسانی شد")
        }
    }

    fun deleteTechnician(technician: Technician) {
        viewModelScope.launch {
            repository.deleteTechnician(technician)
            emitMessage("تکنسین حذف شد")
        }
    }

    // ===================== یادآوری‌ها و تقویم =====================

    val reminders: StateFlow<List<Reminder>> =
        repository.getUpcomingReminders().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, note: String?, dateTimeMillis: Long, ticketId: Long?, onAdded: (Reminder) -> Unit) {
        if (!red.line.tamirkar.util.Validators.isRequired(title)) {
            emitMessage(red.line.tamirkar.util.Validators.REQUIRED_ERROR); return
        }
        if (dateTimeMillis <= System.currentTimeMillis()) {
            emitMessage("تاریخ یادآوری باید در آینده باشد"); return
        }
        viewModelScope.launch {
            val id = repository.addReminder(Reminder(title = title, note = note, dateTimeMillis = dateTimeMillis, ticketId = ticketId))
            emitMessage("یادآوری ثبت شد")
            onAdded(Reminder(id = id, title = title, note = note, dateTimeMillis = dateTimeMillis, ticketId = ticketId))
        }
    }

    fun completeReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.updateReminder(reminder.copy(isDone = true))
            emitMessage("یادآوری انجام‌شده علامت‌گذاری شد")
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            emitMessage("یادآوری حذف شد")
        }
    }

    // ===================== پشتیبان‌گیری =====================

    suspend fun currentBackupData(): red.line.tamirkar.util.BackupData {
        return red.line.tamirkar.util.BackupData(
            customers = repository.getCustomers().first(),
            tickets = repository.getTickets().first(),
            parts = repository.getInventory().first(),
            transactions = repository.getTransactions().first(),
            technicians = repository.getTechnicians().first(),
            reminders = repository.getReminders().first(),
            shopProfile = shopProfile.value
        )
    }

    fun restoreBackup(data: red.line.tamirkar.util.BackupData, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.restoreAll(data.customers, data.tickets, data.parts, data.transactions, data.technicians, data.reminders)
            preferences.saveShopProfile(data.shopProfile)
            emitMessage("بازیابی اطلاعات با موفقیت انجام شد")
            onDone()
        }
    }
}
