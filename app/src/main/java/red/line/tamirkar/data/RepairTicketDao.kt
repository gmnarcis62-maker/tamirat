package red.line.tamirkar.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairTicketDao {
    @Query("SELECT * FROM repair_tickets ORDER BY receivedAt DESC")
    fun getAll(): Flow<List<RepairTicket>>

    @Query("SELECT * FROM repair_tickets WHERE status = :status ORDER BY receivedAt DESC")
    fun getByStatus(status: TicketStatus): Flow<List<RepairTicket>>

    @Query("SELECT * FROM repair_tickets WHERE id = :id")
    suspend fun getById(id: Long): RepairTicket?

    @Insert
    suspend fun insert(ticket: RepairTicket): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tickets: List<RepairTicket>)

    @Query("DELETE FROM repair_tickets")
    suspend fun deleteAll()

    @Update
    suspend fun update(ticket: RepairTicket)

    @Delete
    suspend fun delete(ticket: RepairTicket)

    @Query("""
        SELECT * FROM repair_tickets
        WHERE deviceBrand LIKE '%' || :query || '%'
           OR deviceModel LIKE '%' || :query || '%'
           OR problemDescription LIKE '%' || :query || '%'
        ORDER BY receivedAt DESC
    """)
    fun search(query: String): Flow<List<RepairTicket>>

    @Query("SELECT COUNT(*) FROM repair_tickets WHERE status != 'DELIVERED' AND status != 'CANCELLED'")
    fun getActiveCount(): Flow<Int>
}
