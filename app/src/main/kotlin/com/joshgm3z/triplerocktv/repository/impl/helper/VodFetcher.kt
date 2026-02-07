package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.LIMIT
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
import javax.inject.Inject

class VodFetcher
@Inject
constructor(
    private val vodCategoryDao: VodCategoryDao,
    private val vodStreamsDao: VodStreamsDao,
) {
    lateinit var iptvService: IptvService

    suspend fun fetchContent(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val categories = fetchCategories().subList(0, LIMIT)
        val total = categories.size
        if (total > 0) {
            // Clear existing data only if network call is successful
            vodCategoryDao.deleteAllCategories()
            vodStreamsDao.deleteAllStreams()
        } else {
            onFetch(
                MediaLoadingType.VideoOnDemand,
                LoadingState(0, LoadingStatus.Error)
            )
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
            LoadingState(100, LoadingStatus.Complete)
        )
    }

    private suspend fun fetchCategories(): List<VodCategory> =
        iptvService.getVodCategories(username, password).map {
            VodCategory(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId
            )
        }


    private suspend fun fetchAndStoreContent(vodCategory: VodCategory) {
        val vodStreams = iptvService.getVodStreams(username, password, vodCategory.categoryId)

        vodCategoryDao.insert(vodCategory.apply {
            count = vodStreams.size
            firstStreamIcon = vodStreams.firstOrNull()?.streamIcon
        })
        vodStreamsDao.insertStreams(vodStreams.map {
            VodStream(
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
}