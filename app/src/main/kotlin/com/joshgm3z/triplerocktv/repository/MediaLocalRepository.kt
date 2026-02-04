package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.ui.browse.BrowseType

interface MediaLocalRepository {

    suspend fun fetchAllCategories(): Map<BrowseType, List<VodCategory>>

    suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<VodCategory>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<VodStream>) -> Unit
    )

    suspend fun fetchStreams(categoryId: Int): List<VodStream>

    suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (VodStream) -> Unit,
        onError: (String) -> Unit,
    )
}
