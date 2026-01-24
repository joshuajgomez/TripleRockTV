package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class DemoMediaLocalRepository
@Inject
constructor() : MediaLocalRepository {

    override suspend fun fetchCategories(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(2000)
        onSuccess(
            listOf(
                CategoryEntity(0, "English 4K", 0),
                CategoryEntity(1, "English HD", 0),
                CategoryEntity(2, "English 4HD", 0),
                CategoryEntity(3, "English 4K 2025", 0),
            )
        )
    }

    override suspend fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(2000)
        onSuccess(
            listOf(
                MediaData.sample(),
                MediaData.sample(),
                MediaData.sample(),
                MediaData.sample(),
                MediaData.sample(),
                MediaData.sample(),
            )
        )
    }

    override suspend fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit
    ) {
        delay(2000)
        onSuccess(MediaData.sample())
    }
}