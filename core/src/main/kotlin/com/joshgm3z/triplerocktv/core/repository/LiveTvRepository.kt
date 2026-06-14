package com.joshgm3z.triplerocktv.core.repository

import com.joshgm3z.triplerocktv.core.repository.data.XmlTvProgram
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData

interface LiveTvRepository {
    fun fetchLiveTvGuide(
        onFetch: (List<XmlTvProgram>) -> Unit,
        onError: (String) -> Unit
    )

    suspend fun fetchLiveStream(epgChannelId: String): StreamData?
}