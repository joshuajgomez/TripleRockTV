package com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.stream.MIN_PLAYBACK_DURATION
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "recent_played")
class RecentlyPlayed(
    @PrimaryKey
    val id: Int,
    val seriesId: Int? = null,
    val streamType: StreamType,
    val added: Long,
    val playedDuration: Long,
) {
    override fun toString(): String {
        return "RecentlyPlayed(id=$id, streamType=$streamType, added=$added, seriesId=$seriesId, playedDuration=$playedDuration)"
    }
}

@Dao
interface RecentlyPlayedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentlyPlayed: RecentlyPlayed)

    @Query("DELETE FROM recent_played WHERE id = :id")
    fun delete(id: Int)

    @Query("DELETE FROM recent_played")
    fun deleteAll()

    @Query("SELECT * FROM recent_played")
    fun getAllRecentlyPlayed(): List<RecentlyPlayed>

    @Query("SELECT * FROM recent_played WHERE id = :id")
    fun getRecentlyPlayedById(id: Int): Flow<RecentlyPlayed?>

    @Query("SELECT * FROM recent_played WHERE seriesId = :seriesId")
    fun getRecentlyPlayedBySeriesId(seriesId: Int): Flow<RecentlyPlayed?>

    @Query("SELECT * FROM recent_played WHERE streamType = :type")
    fun getRecentlyPlayedOfType(type: StreamType): List<RecentlyPlayed>

    @Query(
        "SELECT * FROM recent_played WHERE playedDuration > :minPlaybackDuration " +
                "AND streamType = :streamType " +
                "ORDER BY added DESC LIMIT :count"
    )
    suspend fun getRecentlyPlayedByType(
        streamType: StreamType,
        minPlaybackDuration: Long = MIN_PLAYBACK_DURATION,
        count: Int = 5
    ): List<RecentlyPlayed>
}