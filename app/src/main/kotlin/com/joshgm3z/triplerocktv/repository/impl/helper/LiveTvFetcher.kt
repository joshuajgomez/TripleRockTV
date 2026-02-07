package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.LIMIT
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
import javax.inject.Inject

class LiveTvFetcher
@Inject
constructor(
    private val liveTvCategoryDao: LiveTvCategoryDao,
    private val liveTvStreamsDao: LiveTvStreamsDao,
) {
    val empty: Boolean
        get() {
            return liveTvCategoryDao.getAllCategories().isEmpty()
        }
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
            liveTvCategoryDao.deleteAllCategories()
            liveTvStreamsDao.deleteAllStreams()
        } else {
            onFetch(
                MediaLoadingType.LiveTv,
                LoadingState(0, LoadingStatus.Error)
            )
            return
        }

        categories.forEachIndexed { index, it ->
            fetchAndStoreContent(it)
            onFetch(
                MediaLoadingType.LiveTv,
                LoadingState(
                    percent = (index.toFloat() / total * 100).toInt(),
                    status = LoadingStatus.Ongoing
                )
            )
        }
        onFetch(
            MediaLoadingType.LiveTv,
            LoadingState(100, LoadingStatus.Complete)
        )
    }

    private suspend fun fetchCategories(): List<LiveTvCategory> =
        iptvService.getLiveCategories(username, password).map {
            LiveTvCategory(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId
            )
        }.apply {
            Logger.info("getLiveCategories = $this")
        }


    private suspend fun fetchAndStoreContent(liveTvCategory: LiveTvCategory) {
        val liveStreams = iptvService.getLiveStreams(username, password, liveTvCategory.categoryId)
        Logger.info("getLiveStreams = $liveStreams")
        liveTvCategoryDao.insert(liveTvCategory.apply {
            count = liveStreams.size
            firstStreamIcon = liveStreams.firstOrNull()?.streamIcon
        })
        liveTvStreamsDao.insertStreams(liveStreams.map {
            LiveTvStream(
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