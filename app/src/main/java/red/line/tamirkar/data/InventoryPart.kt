package red.line.tamirkar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_parts")
data class InventoryPart(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val compatibleModels: String? = null,
    val quantity: Int = 0,
    val purchasePrice: Double = 0.0,
    val sellPrice: Double = 0.0,
    val lowStockThreshold: Int = 2
)
