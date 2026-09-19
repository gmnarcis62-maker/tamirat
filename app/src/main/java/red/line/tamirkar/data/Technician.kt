package red.line.tamirkar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class Technician(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val pinCode: String,       // پین ۴ رقمی برای تعویض کاربر روی همین دستگاه (نه یک سیستم احراز هویت آنلاین)
    val isOwner: Boolean = false,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
