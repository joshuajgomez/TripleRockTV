package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.data.MediaData
import javax.inject.Inject

class MediaLocalRepositoryImpl @Inject constructor() : MediaLocalRepository {
    override fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}