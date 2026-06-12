package com.joshgm3z.triplerocktv.core.repository.room.favorite

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorite")
class Favorite(
    @PrimaryKey
    val id: Int,
    val streamType: StreamType,
    val added: Long,
)

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM favorite")
    fun deleteAll()

    @Query("SELECT * FROM favorite")
    fun getFavorites(): List<Favorite>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Query(
        "SELECT * FROM favorite " +
                "WHERE streamType = :streamType " +
                "ORDER BY added DESC LIMIT :count"
    )
    suspend fun getFavoritesOfType(streamType: StreamType, count: Int = 5): List<Favorite>
}