package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.MovieMetadata
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnlineDataFetcher
@Inject
constructor(
    private val categoryDataDao: CategoryDataDao,
    private val streamDataDao: StreamDataDao,
) {
    lateinit var iptvService: IptvService

    suspend fun fetchContent(streamType: StreamType) {
        Logger.entry()
        val categories = fetchCategories(streamType)
        if (categories.isNotEmpty()) {
            Logger.info("Updating categories = [${categories.size}]")
            categoryDataDao.replaceData(streamType, categories)
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

    suspend fun updateStreamDataList(categoryId: Int, streamType: StreamType) {
        Logger.warn("categoryId = [$categoryId], streamType = [$streamType]")
        val streams = try {
            when (streamType) {
                StreamType.VideoOnDemand -> iptvService.getVodStreams(
                    username,
                    password,
                    categoryId
                )

                StreamType.LiveTV -> iptvService.getLiveStreams(
                    username,
                    password,
                    categoryId
                )

                else -> emptyList()
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            emptyList()
        }
        Logger.debug("streamType=${streamType}, categoryId=${categoryId}, streams.size=${streams.size}")

        val streamDataList = streams.map {
            StreamData(
                num = it.num,
                name = it.name,
                streamTypeText = it.streamType,
                streamId = it.streamId,
                streamIcon = it.streamIcon,
                categoryId = it.categoryId,
                added = it.added.toLong(),
                streamType = streamType,
                extension = it.containerExtension ?: streamType.defaultExtension(),
                rating = it.rating.parseToFloat(),
                epgChannelId = it.epgChannelId,
            )
        }
        if (streamDataList.isEmpty()) return
        streamDataDao.replaceStreamsOfCategory(streamType, categoryId, streamDataList)
    }

    suspend fun getMovieDataAndUpdate(streamId: Int): MovieMetadata? {
        getMovieData(streamId).let { movieMetaData ->
            val updatedStreamData = withContext(Dispatchers.IO) {
                streamDataDao.getByStreamId(streamId)
            }?.copy(movieMetadata = movieMetaData) ?: return null
            streamDataDao.update(updatedStreamData)
            return movieMetaData
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