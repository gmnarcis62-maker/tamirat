package red.line.tamirkar.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_parts ORDER BY name ASC")
    fun getAll(): Flow<List<InventoryPart>>

    @Query("SELECT * FROM inventory_parts WHERE quantity <= lowStockThreshold")
    fun getLowStock(): Flow<List<InventoryPart>>

    @Insert
    suspend fun insert(part: InventoryPart): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parts: List<InventoryPart>)

    @Query("DELETE FROM inventory_parts")
    suspend fun deleteAll()

    @Update
    suspend fun update(part: InventoryPart)

    @Delete
    suspend fun delete(part: InventoryPart)
}
