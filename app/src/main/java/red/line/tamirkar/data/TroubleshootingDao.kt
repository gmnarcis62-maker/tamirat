package red.line.tamirkar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TroubleshootingDao {
    @Query("SELECT * FROM troubleshooting_guides ORDER BY category ASC")
    fun getAll(): Flow<List<TroubleshootingGuide>>

    @Query("""
        SELECT * FROM troubleshooting_guides
        WHERE title LIKE '%' || :query || '%'
           OR symptoms LIKE '%' || :query || '%'
           OR brand LIKE '%' || :query || '%'
        ORDER BY category ASC
    """)
    fun search(query: String): Flow<List<TroubleshootingGuide>>

    @Query("SELECT * FROM troubleshooting_guides WHERE category = :category ORDER BY title ASC")
    fun getByCategory(category: TroubleshootingCategory): Flow<List<TroubleshootingGuide>>

    @Insert
    suspend fun insertAll(guides: List<TroubleshootingGuide>)

    @Query("SELECT COUNT(*) FROM troubleshooting_guides")
    suspend fun count(): Int
}
