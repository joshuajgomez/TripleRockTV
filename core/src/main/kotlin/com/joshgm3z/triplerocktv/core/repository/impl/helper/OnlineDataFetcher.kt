package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.LoadingState
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.core.repository.impl.REQUEST_DELAY
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.encodeWithSound
import kotlinx.coroutines.delay
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
    ) {
        Logger.entry()
        val categories = fetchCategories(streamType)

        val streamDataListToStore = mutableListOf<StreamData>()
        val categoriesToStore = mutableListOf<CategoryData>()

        var errorMessage = ""
        try {
            categories.forEachIndexed { index, it ->
                val list = fetchStreamDataList(it)
                if (list.isNotEmpty()) {
                    categoriesToStore.add(it.apply {
                        count = list.size
                        firstStreamIcon = list.firstOrNull()?.streamIcon
                    })
                    streamDataListToStore.addAll(list)
                }
                onFetch(
                    LoadingState(
                        percent = (index.toFloat() / categories.size * 100).toInt(),
                        status = LoadingStatus.Ongoing,
                    )
                )
                delay(REQUEST_DELAY)
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            e.printStackTrace()
            errorMessage = e.message.toString()
        }

        if (categoriesToStore.isNotEmpty() && streamDataListToStore.isNotEmpty()) {
            Logger.info("storing categories = [${categoriesToStore.size}], streams = [${streamDataListToStore.size}]")

            categoryDataDao.replaceData(streamType, categoriesToStore)
            streamDataDao.replaceData(streamType, streamDataListToStore)
        }
        when {
            categoriesToStore.isEmpty() || streamDataListToStore.isEmpty() -> onFetch(
                LoadingState(
                    status = LoadingStatus.Error,
                    error = "Unable to update. Try again in a few min"
                )
            )

            errorMessage.isNotEmpty() -> onFetch(
                LoadingState(
                    status = LoadingStatus.Error,
                    error = "Partially updated. Try again to get full content."
                )
            )

            else -> onFetch(
                LoadingState(
                    percent = 100,
                    status = LoadingStatus.Complete
                )
            )
        }
    }

    private suspend fun fetchCategories(streamType: StreamType): List<CategoryData> {
        val categoryDataList = try {
            when (streamType) {
                StreamType.VideoOnDemand -> iptvService.getVodCategories(username, password)
                StreamType.LiveTV -> iptvService.getLiveCategories(username, password)
                else -> return emptyList()
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            e.printStackTrace()
            emptyList()
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


    private suspend fun fetchStreamDataList(categoryData: CategoryData): List<StreamData> {
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
        Logger.debug("streamType=${categoryData.streamType}, categoryId=${categoryData.categoryId}, streams.size=${streams.size}")

        return streams.map {
            StreamData(
                num = it.num,
                name = it.name,
                soundEncodedName = it.name.encodeWithSound(),
                streamTypeText = it.streamType,
                streamId = it.streamId,
                streamIcon = it.streamIcon,
                categoryId = it.categoryId,
                added = it.added,
                streamType = categoryData.streamType,
                extension = it.containerExtension ?: categoryData.streamType.defaultExtension(),
                rating = it.rating.parseToFloat(),
                epgChannelId = it.epgChannelId,
            )
        }
    }

    suspend fun getMovieDataAndUpdate(streamId: Int, streamType: StreamType) {
        if (streamType != StreamType.VideoOnDemand) return
        getMovieData(streamId).let { movieMetaData ->
            val updatedStreamData = streamDataDao.getByStreamId(streamId)
                .copy(movieMetadata = movieMetaData)
            streamDataDao.update(updatedStreamData)
        }
    }

    suspend fun getMovieData(streamId: Int): MovieMetadata? {
        Logger.debug("streamId = [${streamId}]")
        return try {
            iptvService.getVodInfo(streamId).info.let {
                MovieMetadata(
                    description = it.description,
                    backPosterUrl = it.backdropPath?.firstOrNull(),
                    cast = it.cast,
                    director = it.director,
                    actors = it.actors,
                    genre = it.genre,
                    totalDurationMs = (it.durationSecs?.toLong() ?: 0L) * 1000L,
                )
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            e.printStackTrace()
            null
        }
    }
}

private fun StreamType.defaultExtension(): String = when (this) {
    StreamType.LiveTV -> "ts"
    else -> ""
}

fun String?.parseToFloat(): Float {
    val value = this?.toFloatOrNull() ?: 0f
    return "%.1f".format(value).toFloat()
}