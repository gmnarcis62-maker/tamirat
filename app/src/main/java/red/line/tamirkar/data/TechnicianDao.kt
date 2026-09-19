package red.line.tamirkar.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians ORDER BY isOwner DESC, name ASC")
    fun getAll(): Flow<List<Technician>>

    @Query("SELECT * FROM technicians WHERE active = 1 ORDER BY isOwner DESC, name ASC")
    fun getActive(): Flow<List<Technician>>

    @Insert
    suspend fun insert(technician: Technician): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(technicians: List<Technician>)

    @Query("DELETE FROM technicians WHERE isOwner = 0")
    suspend fun deleteAllExceptOwner()

    @Update
    suspend fun update(technician: Technician)

    @Delete
    suspend fun delete(technician: Technician)

    @Query("SELECT COUNT(*) FROM technicians")
    suspend fun count(): Int
}
