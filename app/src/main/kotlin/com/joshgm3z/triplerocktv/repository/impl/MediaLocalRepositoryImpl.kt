package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import com.joshgm3z.triplerocktv.ui.browse.BrowseType
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor(
    private val vodCategoryDao: VodCategoryDao,
    private val vodStreamsDao: VodStreamsDao,
) : MediaLocalRepository {
    override suspend fun fetchAllCategories(): Map<BrowseType, List<VodCategory>> {
        return mapOf(
            BrowseType.VideoOnDemand to emptyList(),
            BrowseType.Series to emptyList(),
            BrowseType.EPG to emptyList(),
            BrowseType.LiveTV to emptyList(),
        )
    }

    companion object {
        private const val TAG: String = "MediaLocalRepositoryImpl"
    }

    override suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<VodCategory>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.i(TAG, "fetchCategories: entry")
        vodCategoryDao.getAllCategories().collect { categories ->
            Log.i(TAG, "fetchCategories: $categories")
            onSuccess(categories)
        }
    }

    override suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<VodStream>) -> Unit
    ) {
        onSearchResult(vodStreamsDao.searchStreams(name))
    }

    override suspend fun fetchStreams(categoryId: Int): List<VodStream> {
        return vodStreamsDao.getAllStreams(categoryId)
    }

    override suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (VodStream) -> Unit,
        onError: (String) -> Unit
    ) {
        vodStreamsDao.getStream(streamId).collect { stream ->
            Log.i(TAG, "fetchMediaDataById: $stream")
            onSuccess(stream)
        }
    }

}