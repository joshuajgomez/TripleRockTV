package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.joshgm3z.triplerocktv.ui.browse.StreamType

@Dao
interface StreamDataDao {
    @Query("SELECT * FROM stream_data WHERE categoryId = :categoryId AND type = :type")
    fun getAllFromCategoryAndType(
        categoryId: Int,
        type: StreamType
    ): List<StreamData>

    @Query("SELECT * FROM stream_data WHERE name LIKE '%' || :streamName || '%'")
    fun searchByName(streamName: String): List<StreamData>

    @Query("SELECT * FROM stream_data WHERE streamId = :streamId")
    fun getByStreamId(streamId: Int): StreamData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(streams: List<StreamData>)

    @Update
    suspend fun update(streamData: StreamData)

    @Query("DELETE FROM stream_data WHERE type = :type")
    suspend fun deleteAllOfType(type: StreamType)

    @Query("DELETE FROM stream_data")
    suspend fun deleteAll()

    @Query("SELECT * FROM stream_data WHERE lastPlayed > 0 ORDER BY lastPlayed DESC LIMIT 10")
    fun getLastPlayed10(): List<StreamData>
}
