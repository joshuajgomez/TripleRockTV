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
data class RecentlyPlayed(
    @PrimaryKey
    val id: Int,
    val seriesId: Int? = null,
    val streamType: StreamType? = null,
    val added: Long? = null,
    val playedDurationMs: Long? = null,
    var subtitleUrl: String? = null,
    var subtitleLanguage: String? = null,
    var subtitleTitle: String? = null,
) {
    override fun toString(): String {
        return "\nRecentlyPlayed(" +
                "\nid=$id, seriesId=$seriesId, streamType=$streamType, added=$added, " +
                "\nplayedDurationMs=$playedDurationMs, " +
                "\nsubtitleTitle=$subtitleTitle, subtitleLanguage=$subtitleLanguage, " +
                "\nsubtitleUrl=$subtitleUrl)"
    }
}

@Dao
interface RecentlyPlayedDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentlyPlayed: RecentlyPlayed)

    @Query("DELETE FROM recent_played WHERE id = :id AND streamType = :streamType")
    suspend fun delete(id: Int, streamType: StreamType)

    @Query("DELETE FROM recent_played")
    fun deleteAll()

    @Query("SELECT * FROM recent_played")
    fun getAllRecentlyPlayed(): List<RecentlyPlayed>

    @Query("SELECT * FROM recent_played WHERE id = :id")
    fun recentlyPlayedFlow(id: Int): Flow<RecentlyPlayed?>

    @Query("SELECT * FROM recent_played WHERE id = :id")
    suspend fun getRecentlyPlayedById(id: Int): RecentlyPlayed?

    @Query("SELECT * FROM recent_played WHERE seriesId = :seriesId ORDER BY added DESC LIMIT 1")
    fun recentlyPlayedSeriesFlow(seriesId: Int): Flow<RecentlyPlayed?>

    @Query("SELECT * FROM recent_played WHERE streamType = :type ORDER BY added DESC LIMIT 5")
    fun recentlyPlayedFlowOfType(type: StreamType): Flow<List<RecentlyPlayed>>

    @Query(
        """
        SELECT * FROM recent_played
        WHERE streamType = :type
        AND seriesId IS NOT NULL 
        GROUP BY seriesId 
        HAVING added = MAX(added)
        ORDER BY added DESC
        LIMIT 5
        """
    )
    fun recentlyPlayedSeriesFlow(type: StreamType = StreamType.Series): Flow<List<RecentlyPlayed>>

    @Query(
        "SELECT * FROM recent_played WHERE playedDurationMs > :minPlaybackDuration " +
                "AND streamType = :streamType " +
                "ORDER BY added DESC LIMIT :count"
    )
    suspend fun getRecentlyPlayedByType(
        streamType: StreamType,
        minPlaybackDuration: Long = MIN_PLAYBACK_DURATION,
        count: Int = 5
    ): List<RecentlyPlayed>


    @Query("UPDATE recent_played SET subtitleUrl = :url WHERE id = :streamId")
    suspend fun setSubtitleUrl(streamId: Int, url: String)

    @Query("UPDATE recent_played SET subtitleLanguage = :language, subtitleTitle = :title WHERE id = :streamId")
    suspend fun setSubtitleLanguage(streamId: Int, language: String, title: String)

}