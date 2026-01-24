package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.data.IptvCategory
import com.joshgm3z.triplerocktv.repository.room.CategoryDao
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.StreamEntity
import com.joshgm3z.triplerocktv.repository.room.StreamsDao
import javax.inject.Inject

class MediaOnlineRepositoryImpl
@Inject constructor(
    private val iptvService: IptvService,
    private val categoryDao: CategoryDao,
    private val streamsDao: StreamsDao,
) : MediaOnlineRepository {
    companion object {
        private const val TAG: String = "MediaOnlineRepositoryImpl"
    }

    private val username: String = "jgomez554"
    private val password: String = "38333563"

    override suspend fun fetchContent(onFetch: (MediaLoadingType, LoadingState) -> Unit) {
        try {
            val categories = fetchCategories()
            val total = categories.size
            Log.i(TAG, "fetchContent: $total categories found")
            categories.forEachIndexed { index, it ->
                if (index == 3) return@forEachIndexed // limit to 3 categories for now
                fetchContent(it.categoryId)
                Log.i(TAG, "fetchContent: index=$index, percent=${index * 100 / total}")
                onFetch(
                    MediaLoadingType.VideoOnDemand,
                    LoadingState(index * 100 / total, LoadingStatus.Ongoing)
                )
            }
            onFetch(
                MediaLoadingType.VideoOnDemand,
                LoadingState(0, LoadingStatus.Complete)
            )
        } catch (e: Exception) {
            Log.e(TAG, "fetchContent: error=${e.message}")
            onFetch(
                MediaLoadingType.VideoOnDemand,
                LoadingState(0, LoadingStatus.Error, e.message)
            )
        }
    }

    private suspend fun fetchContent(categoryId: Int) {
        Log.i(TAG, "fetchContent: entry")
        val vodStreams = iptvService.getVodStreams(username, password, categoryId)
        Log.i(TAG, "fetchContent: $vodStreams")
        streamsDao.insertStreams(vodStreams.map {
            StreamEntity(
                num = it.num,
                name = it.name,
                streamType = it.streamType,
                streamId = it.streamId,
                streamIcon = it.streamIcon,
                categoryId = it.categoryId,
                added = it.added,
            )
        })
    }

    private suspend fun fetchCategories(): List<IptvCategory> {
        val vodCategories = iptvService.getVodCategories(username, password)
        categoryDao.insertCategories(vodCategories.map {
            CategoryEntity(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId
            )
        })
        return vodCategories
    }
}
