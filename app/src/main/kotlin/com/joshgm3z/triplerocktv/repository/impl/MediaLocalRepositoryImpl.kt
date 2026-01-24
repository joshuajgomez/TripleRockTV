package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.repository.room.CategoryDao
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : MediaLocalRepository {

    companion object {
        private const val TAG: String = "MediaLocalRepositoryImpl"
    }

    override suspend fun fetchCategories(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.i(TAG, "fetchCategories: entry")
        categoryDao.getAllCategories().collect { categories ->
            Log.i(TAG, "fetchCategories: $categories")
            onSuccess(categories)
        }
    }

    override suspend fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit
    ) {
    }

    override suspend fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}