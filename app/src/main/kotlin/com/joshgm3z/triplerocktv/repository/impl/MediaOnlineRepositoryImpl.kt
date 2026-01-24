package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import javax.inject.Inject

class MediaOnlineRepositoryImpl
@Inject constructor(
    private val iptvService: IptvService
) : MediaOnlineRepository {
    companion object {
        private const val TAG: String = "MediaOnlineRepositoryImpl"
    }

    private val username: String = "jgomez554"
    private val password: String = "38333563"

    override suspend fun fetchContent(onFetch: (MediaLoadingType, LoadingState) -> Unit) {
        fetchCategories()
    }

    private suspend fun fetchCategories() {
        val vodCategories = iptvService.getVodCategories(username, password)
    }
}