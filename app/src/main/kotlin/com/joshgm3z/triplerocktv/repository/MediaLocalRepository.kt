package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.data.MediaData
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity

interface MediaLocalRepository {

    suspend fun fetchCategories(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit,
    )
}
