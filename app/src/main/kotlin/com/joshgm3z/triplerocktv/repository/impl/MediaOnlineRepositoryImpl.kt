package com.joshgm3z.triplerocktv.repository.impl

import android.util.Log
import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.room.CategoryDao
import com.joshgm3z.triplerocktv.repository.room.CategoryEntity
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.retrofit.Secrets
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
        private const val LIMIT = 3
    }

    private val username = Secrets.username
    private val password = Secrets.password

    override suspend fun fetchContent(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        try {
            val categories = fetchCategories().subList(0, LIMIT)
            val total = categories.size
            if (total > 0) {
                // Clear existing data only if network call is successful
                categoryDao.deleteAllCategories()
                streamsDao.deleteAllStreams()
            } else {
                onError("Unable to fetch categories.", "No categories found.")
                return
            }

            categories.forEachIndexed { index, it ->
                fetchAndStoreContent(it)
                onFetch(
                    MediaLoadingType.VideoOnDemand,
                    LoadingState(
                        percent = (index.toFloat() / total * 100).toInt(),
                        status = LoadingStatus.Ongoing
                    )
                )
            }
            onFetch(
                MediaLoadingType.VideoOnDemand,
                LoadingState(0, LoadingStatus.Complete)
            )
        } catch (e: Exception) {
            Log.e(TAG, "fetchContent: error=${e.message}")
            onError("Unable to fetch categories.", e.message ?: "")
        }
    }

    private suspend fun fetchAndStoreContent(categoryEntity: CategoryEntity) {
        val vodStreams = iptvService.getVodStreams(username, password, categoryEntity.categoryId)

        categoryDao.insert(categoryEntity.apply { count = vodStreams.size })
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

    private suspend fun fetchCategories(): List<CategoryEntity> {
        return iptvService.getVodCategories(username, password).map {
            CategoryEntity(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId
            )
        }
    }
}
