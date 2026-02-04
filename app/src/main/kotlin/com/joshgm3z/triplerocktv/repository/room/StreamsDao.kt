package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joshgm3z.triplerocktv.repository.data.IptvSeries
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamsDao {
    @Query("SELECT * FROM streams WHERE categoryId = :categoryId LIMIT 30")
    fun getAllStreams(categoryId: Int): List<StreamEntity>

    @Query("SELECT * FROM streams WHERE name LIKE :streamName LIMIT 30")
    fun searchStreams(streamName: String): List<StreamEntity>

    @Query("SELECT * FROM streams WHERE streamId = :streamId")
    fun getStream(streamId: Int): Flow<StreamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<StreamEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(streams: List<SeriesEntity>)

    @Query("DELETE FROM streams")
    suspend fun deleteAllStreams()
}
