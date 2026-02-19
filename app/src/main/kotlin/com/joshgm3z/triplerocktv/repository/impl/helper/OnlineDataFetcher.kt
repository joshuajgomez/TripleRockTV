package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.LIMIT
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamData
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.util.Logger
import java.util.stream.Stream
import javax.inject.Inject

class OnlineDataFetcher
@Inject
constructor(
    private val categoryDataDao: CategoryDataDao,
    private val streamDataDao: StreamDataDao,
) {
    lateinit var iptvService: IptvService

    suspend fun fetchContent(
        streamType: StreamType,
        onFetch: (LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val categories = fetchCategories(streamType).subList(0, LIMIT)
        val total = categories.size
        if (total > 0) {
            // Clear existing data only if network call is successful
            categoryDataDao.deleteAllOfType(streamType)
            streamDataDao.deleteAllOfType(streamType)
        } else {
            onFetch(LoadingState(0, LoadingStatus.Error))
            return
        }

        categories.forEachIndexed { index, it ->
            fetchAndStoreContent(it)
            onFetch(
                LoadingState(
                    percent = (index.toFloat() / total * 100).toInt(),
                    status = LoadingStatus.Ongoing
                )
            )
        }
        onFetch(LoadingState(100, LoadingStatus.Complete))
    }

    private suspend fun fetchCategories(streamType: StreamType): List<CategoryData> {
        val categoryDataList = when (streamType) {
            StreamType.VideoOnDemand -> iptvService.getVodCategories(username, password)
            StreamType.LiveTV -> iptvService.getLiveCategories(username, password)
            else -> return emptyList()
        }
        return categoryDataList.map {
            CategoryData(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId,
                streamType = streamType
            )
        }
    }


    private suspend fun fetchAndStoreContent(categoryData: CategoryData) {
        val streams = when (categoryData.streamType) {
            StreamType.VideoOnDemand -> iptvService.getVodStreams(
                username,
                password,
                categoryData.categoryId
            )

            StreamType.LiveTV -> iptvService.getLiveStreams(
                username,
                password,
                categoryData.categoryId
            )

            else -> emptyList()
        }

        categoryDataDao.insert(categoryData.apply {
            count = streams.size
            firstStreamIcon = streams.firstOrNull()?.streamIcon
        })
        streamDataDao.insertAll(streams.map {
            StreamData(
                num = it.num,
                name = it.name,
                streamTypeText = it.streamType,
                streamId = it.streamId,
                streamIcon = it.streamIcon,
                categoryId = it.categoryId,
                added = it.added,
                streamType = categoryData.streamType,
                extension = if (categoryData.streamType == StreamType.VideoOnDemand) "mkv" else "m3u8"
            )
        })
    }
}