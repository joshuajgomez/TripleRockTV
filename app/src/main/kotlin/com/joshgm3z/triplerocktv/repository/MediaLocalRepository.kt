package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.ui.browse.BrowseType

interface MediaLocalRepository {

    suspend fun fetchCategories(
        browseType: BrowseType,
        onSuccess: (List<CategoryEntity>) -> Unit,
        onError: (String) -> Unit,
    )

    suspend fun searchStreamByName(
        name: String,
        onSearchResult: (List<StreamEntity>) -> Unit
    )

    suspend fun fetchStreams(categoryId: Int): List<StreamEntity>

    suspend fun fetchMediaDataById(
        streamId: Int,
        onSuccess: (StreamEntity) -> Unit,
        onError: (String) -> Unit,
    )
}
