package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream

const val SEARCH_LIMIT = 10

interface SearchRepository {
    suspend fun searchStreamByName(name: String, streamType: StreamType): List<StreamData>

    suspend fun searchSeriesByName(name: String): List<SeriesStream>

    suspend fun addSearchText(searchText: String)

    suspend fun getSearchTextList(): List<String>
}