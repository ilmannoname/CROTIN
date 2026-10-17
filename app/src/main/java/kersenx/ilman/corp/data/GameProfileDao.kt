package kersenx.ilman.corp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameProfileDao {
    @Query("SELECT * FROM game_profiles")
    fun getAllProfiles(): Flow<List<GameProfile>>

    @Query("SELECT * FROM game_profiles WHERE packageName = :pkg LIMIT 1")
    suspend fun getProfile(pkg: String): GameProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: GameProfile)

    @Delete
    suspend fun delete(profile: GameProfile)
}
