package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream

interface SearchRepository {
    suspend fun searchStreamByName(name: String, streamType: StreamType): List<StreamData>

    suspend fun searchSeriesByName(name: String): List<SeriesStream>

    suspend fun addSearchText(searchText: String)

    suspend fun getSearchTextList(): List<String>
}