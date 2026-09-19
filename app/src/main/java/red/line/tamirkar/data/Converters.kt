package red.line.tamirkar.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStatus(status: TicketStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): TicketStatus = TicketStatus.valueOf(value)

    @TypeConverter
    fun fromCategory(category: TroubleshootingCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): TroubleshootingCategory = TroubleshootingCategory.valueOf(value)

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String = category.name

    @TypeConverter
    fun toTransactionCategory(value: String): TransactionCategory = TransactionCategory.valueOf(value)
}
