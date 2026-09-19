package red.line.tamirkar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TicketStatus(val label: String) {
    PENDING("در انتظار بررسی"),
    IN_PROGRESS("در حال تعمیر"),
    READY("آماده تحویل"),
    DELIVERED("تحویل شده"),
    CANCELLED("لغو شده")
}

@Entity(tableName = "repair_tickets")
data class RepairTicket(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val deviceBrand: String,
    val deviceModel: String,
    val problemDescription: String,
    val accessories: String? = null,
    val agreedPrice: Double = 0.0,
    val finalPrice: Double? = null,
    val status: TicketStatus = TicketStatus.PENDING,
    val receivedAt: Long = System.currentTimeMillis(),
    val deliveredAt: Long? = null,
    val technicianNote: String? = null,
    val incomeRecorded: Boolean = false, // برای جلوگیری از ثبت تکراری در ماژول حسابداری
    val technicianId: Long? = null,      // تکنسین مسئول این فیش
    val beforePhotoUri: String? = null,  // عکس قبل از تعمیر
    val afterPhotoUri: String? = null,   // عکس بعد از تعمیر
    val signatureUri: String? = null,    // امضای دیجیتال مشتری هنگام تحویل
    val expectedDeliveryDate: Long? = null // تاریخ موعد تحویل (برای یادآوری)
)
