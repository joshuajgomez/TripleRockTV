package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.SearchRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.room.SearchText
import com.joshgm3z.triplerocktv.repository.room.SearchTextDao
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.util.Logger
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchTextDao: SearchTextDao,
    private val streamDataDao: StreamDataDao,
) : SearchRepository {
    override suspend fun searchStreamByName(
        name: String,
        streamType: StreamType
    ): List<StreamData> = when (streamType) {
        StreamType.Series -> emptyList()
        else -> streamDataDao.searchByName(name)
    }.apply {
        Logger.info("searchStreamByName($name, $streamType): $this")
    }

    override suspend fun addSearchText(searchText: String) {
        searchTextDao.insert(SearchText(text = searchText))
    }

    override suspend fun getSearchTextList(): List<String> {
        return searchTextDao.getAll().map { it.text }
    }
}