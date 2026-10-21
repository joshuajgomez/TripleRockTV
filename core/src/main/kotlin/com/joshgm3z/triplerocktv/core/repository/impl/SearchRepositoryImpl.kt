package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.SearchHint
import com.joshgm3z.triplerocktv.core.repository.room.SearchHintDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchHintDao: SearchHintDao,
    private val streamDataDao: StreamDataDao,
    private val seriesStreamsDao: SeriesStreamsDao,
) : SearchRepository {

    override suspend fun searchStreamByName(name: String): List<StreamData> =
        streamDataDao.searchByName(name).apply {
            Logger.info("searchStreamByName($name): $this")
        }

    override suspend fun searchSeriesByName(name: String): List<SeriesStream> {
        return seriesStreamsDao.searchStreams(name)
    }

    override suspend fun addSearchText(searchText: String) {
        searchHintDao.insert(SearchHint(text = searchText))
    }

    override fun searchHintsFlow(): Flow<List<String>> {
        return searchHintDao.searchHintsFlow().map { it -> it.map { it.text } }
    }
}