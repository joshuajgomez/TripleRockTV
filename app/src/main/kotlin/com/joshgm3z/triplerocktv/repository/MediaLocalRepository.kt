package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.home.TopbarItem

interface MediaLocalRepository {

    suspend fun fetchCategories(
        topbarItem: TopbarItem,
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchAllMediaData(
        categoryId: Int,
        onSuccess: (List<StreamEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (StreamEntity) -> Unit,
        onError: (String) -> Unit,
    )
}
