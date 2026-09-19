package red.line.tamirkar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType(val label: String) {
    INCOME("درآمد"),
    EXPENSE("هزینه")
}

enum class TransactionCategory(val label: String, val type: TransactionType) {
    REPAIR_INCOME("درآمد تعمیر", TransactionType.INCOME),
    PART_SALE("فروش قطعه", TransactionType.INCOME),
    OTHER_INCOME("سایر درآمدها", TransactionType.INCOME),
    PART_PURCHASE("خرید قطعه", TransactionType.EXPENSE),
    RENT("اجاره مغازه", TransactionType.EXPENSE),
    UTILITIES("قبوض", TransactionType.EXPENSE),
    SALARY("حقوق پرسنل", TransactionType.EXPENSE),
    OTHER_EXPENSE("سایر هزینه‌ها", TransactionType.EXPENSE)
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val note: String? = null,
    val date: Long = System.currentTimeMillis(),
    val relatedTicketId: Long? = null // در صورت ثبت خودکار از تحویل فیش تعمیر
)
