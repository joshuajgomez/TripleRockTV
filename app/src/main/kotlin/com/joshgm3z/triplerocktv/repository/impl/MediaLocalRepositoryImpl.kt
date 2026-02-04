package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.room.CategoryDao
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.repository.room.StreamsDao
import com.joshgm3z.triplerocktv.ui.browse.BrowseType
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val streamsDao: StreamsDao,
) : MediaLocalRepository {

    companion object {
        private const val TAG: String = "MediaLocalRepositoryImpl"
    }

    override suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.i(TAG, "fetchCategories: entry")
        categoryDao.getAllCategories().collect { categories ->
            Log.i(TAG, "fetchCategories: $categories")
            onSuccess(categories)
        }
    }

    override suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<StreamEntity>) -> Unit
    ) {
        onSearchResult(streamsDao.searchStreams(name))
    }

    override suspend fun fetchStreams(categoryId: Int): List<StreamEntity> {
        return streamsDao.getAllStreams(categoryId)
    }

    override suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (StreamEntity) -> Unit,
        onError: (String) -> Unit
    ) {
        streamsDao.getStream(streamId).collect { stream ->
            Log.i(TAG, "fetchMediaDataById: $stream")
            onSuccess(stream)
        }
    }

}