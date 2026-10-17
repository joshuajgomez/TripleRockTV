package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import kotlinx.coroutines.flow.Flow

const val SEARCH_LIMIT = 10

interface SearchRepository {
    suspend fun searchStreamByName(name: String, streamType: StreamType): List<StreamData>

    suspend fun searchSeriesByName(name: String): List<SeriesStream>

    suspend fun addSearchText(searchText: String)

    fun searchHintsFlow(): Flow<List<String>>
}