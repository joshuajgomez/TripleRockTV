package com.joshgm3z.triplerocktv.repository

import com.joshgm3z.triplerocktv.repository.data.MediaData

interface MediaRepository {
    fun fetchAllMediaData(
        onSuccess: (List<MediaData>) -> Unit,
        onError: (String) -> Unit,
    )

    fun fetchMediaDataById(
        id: String,
        onSuccess: (MediaData) -> Unit,
        onError: (String) -> Unit,
    )
}
