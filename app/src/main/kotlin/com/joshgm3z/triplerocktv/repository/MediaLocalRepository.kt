package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity

interface MediaLocalRepository {

    suspend fun fetchCategories(
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchAllMediaData(
        categoryId: Int,
        onSuccess: (List<StreamEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchMediaDataById(
        id: String,
        onSuccess: (StreamEntity) -> Unit,
        onError: (String) -> Unit,
    )
}
