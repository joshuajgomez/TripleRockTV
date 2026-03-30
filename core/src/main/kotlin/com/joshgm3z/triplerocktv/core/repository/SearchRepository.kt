package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.room.StreamData

interface SearchRepository {
    suspend fun searchStreamByName(name: String, streamType: StreamType): List<StreamData>

    suspend fun addSearchText(searchText: String)

    suspend fun getSearchTextList(): List<String>
}