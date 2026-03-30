package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.room.SearchHint
import com.joshgm3z.triplerocktv.core.repository.room.SearchHintDao
import com.joshgm3z.triplerocktv.core.repository.room.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.core.util.Logger
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchHintDao: SearchHintDao,
    private val streamDataDao: StreamDataDao,
) : SearchRepository {
    override suspend fun searchStreamByName(
        name: String,
        streamType: StreamType
    ): List<StreamData> = when (streamType) {
        StreamType.Series -> emptyList()
        StreamType.VideoOnDemand -> streamDataDao.searchByName(name)
        else -> emptyList()
    }.apply {
        Logger.info("searchStreamByName($name, $streamType): $this")
    }

    override suspend fun addSearchText(searchText: String) {
        searchHintDao.insert(SearchHint(text = searchText))
    }

    override suspend fun getSearchTextList(): List<String> {
        return searchHintDao.getSearchHints().map { it.text }
    }
}