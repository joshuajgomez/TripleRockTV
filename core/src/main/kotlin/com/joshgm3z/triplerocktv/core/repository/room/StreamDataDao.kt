package com.joshgm3z.triplerocktv.core.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.joshgm3z.triplerocktv.core.repository.StreamType
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamDataDao {
    @Query("SELECT * FROM stream_data " +
            "WHERE categoryId = :categoryId AND streamType = :streamType " +
            "ORDER BY added DESC")
    fun getAllFromCategoryAndType(
        categoryId: Int,
        streamType: StreamType
    ): List<StreamData>

    @Query("SELECT * FROM stream_data WHERE name LIKE '%' || :streamName || '%' LIMIT :limit")
    fun searchByName(streamName: String, limit: Int = 5): List<StreamData>

    @Query("SELECT * FROM stream_data WHERE streamId = :streamId")
    fun getByStreamId(streamId: Int): StreamData

    @Query("SELECT * FROM stream_data WHERE streamId = :streamId")
    fun streamDataFlow(streamId: Int): Flow<StreamData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(streams: List<StreamData>)

    @Transaction
    suspend fun replaceData(streamType: StreamType, streamDataList: List<StreamData>) {
        deleteAllOfType(streamType)
        insertAll(streamDataList)
    }

    @Update
    suspend fun update(streamData: StreamData)

    @Query("DELETE FROM stream_data WHERE streamType = :streamType")
    suspend fun deleteAllOfType(streamType: StreamType)

    @Query("DELETE FROM stream_data")
    suspend fun deleteAll()

    @Query(
        "SELECT * FROM stream_data WHERE lastPlayed > 0 " +
                "AND playedDuration > :minPlaybackDuration " +
                "AND streamType = :streamType " +
                "AND totalDurationMs - playedDuration > :minDurationLeft " +
                "ORDER BY lastPlayed DESC LIMIT 5"
    )
    suspend fun getLastPlayed10(
        streamType: StreamType,
        minPlaybackDuration: Long = MIN_PLAYBACK_DURATION,
        minDurationLeft: Long = MIN_DURATION_LEFT
    ): List<StreamData>

    @Query("SELECT * FROM stream_data " +
            "WHERE inMyList IS true " +
            "AND streamType = :streamType " +
            "ORDER BY timeAddedToList DESC LIMIT 5")
    suspend fun getMyList5(streamType: StreamType): List<StreamData>

    @Query("SELECT * FROM stream_data ORDER BY added DESC LIMIT 10")
    suspend fun getNewlyAdded10(): List<StreamData>

    @Query("UPDATE stream_data SET lastPlayed = :lastPlayed WHERE streamId = :streamId")
    suspend fun updateLastPlayedTimestamp(
        streamId: Int,
        lastPlayed: Long = System.currentTimeMillis()
    )

    @Query("UPDATE stream_data SET playedDuration = :playedDuration WHERE streamId = :streamId")
    suspend fun updatePlayedDuration(streamId: Int, playedDuration: Long)

    @Query("UPDATE stream_data SET subtitleUrl = :url WHERE streamId = :streamId")
    suspend fun updateSubtitleUrl(streamId: Int, url: String)

    @Query("UPDATE stream_data SET inMyList = :add, timeAddedToList = :timeAddedToList WHERE streamId = :streamId")
    suspend fun updateMyList(
        streamId: Int,
        add: Boolean,
        timeAddedToList: Long = System.currentTimeMillis()
    )

    @Query("UPDATE stream_data SET subtitleLanguage = :language, subtitleTitle = :title WHERE streamId = :streamId")
    suspend fun updateSubtitleLanguage(streamId: Int, language: String, title: String)

}