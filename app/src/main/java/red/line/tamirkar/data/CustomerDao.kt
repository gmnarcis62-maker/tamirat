package red.line.tamirkar.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE fullName LIKE '%' || :query || '%' OR phoneNumber LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Customer>>

    @Insert
    suspend fun insert(customer: Customer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<Customer>)

    @Query("DELETE FROM customers")
    suspend fun deleteAll()

    @Update
    suspend fun update(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)
}
