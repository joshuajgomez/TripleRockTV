package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.LoadingState
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.core.repository.impl.REQUEST_DELAY
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.repository.room.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.delay
import javax.inject.Inject

class SeriesFetcher
@Inject
constructor(
    private val categoryDataDao: CategoryDataDao,
    private val seriesStreamsDao: SeriesStreamsDao,
) {
    lateinit var iptvService: IptvService

    suspend fun fetchContent(
        limit: Int? = null,
        onFetch: (LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val categories = fetchSeriesCategories().let {
            if (limit != null) it.subList(0, limit) else it
        }
        val total = categories.size

        val seriesStreamListToStore = mutableListOf<SeriesStream>()
        val categoriesToStore = mutableListOf<CategoryData>()

        var errorMessage = ""
        try {
            categories.forEachIndexed { index, it ->
                val list = fetchSeries(it)
                if (list.isNotEmpty()) {
                    categoriesToStore.add(
                        it.apply {
                            count = list.size
                            firstStreamIcon = list.firstOrNull()?.coverImageUrl
                        }
                    )
                    seriesStreamListToStore.addAll(list)
                }

                onFetch(
                    LoadingState(
                        percent = (index.toFloat() / total * 100).toInt(),
                        status = LoadingStatus.Ongoing
                    )
                )
                delay(REQUEST_DELAY)
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            e.printStackTrace()
            errorMessage = e.message.toString()
        }

        if (seriesStreamListToStore.isNotEmpty() && categoriesToStore.isNotEmpty()) {
            Logger.info("storing categories = [${categoriesToStore.size}], streams = [${seriesStreamListToStore.size}]")

            categoryDataDao.replaceData(StreamType.Series, categoriesToStore)
            seriesStreamsDao.replaceData(seriesStreamListToStore)
        }

        when {
            categoriesToStore.isEmpty() || seriesStreamListToStore.isEmpty() -> onError(
                "Unable to sync content",
                "Try again later"
            )
            errorMessage.isNotEmpty() -> onError(
                "Unable to fully sync content",
                "You can still use the app. But some content might not be available."
            )
            else -> onFetch(
                LoadingState(
                    percent = 100,
                    status = LoadingStatus.Complete
                )
            )
        }
    }

    private suspend fun fetchSeriesCategories(): List<CategoryData> =
        iptvService.getSeriesCategories(username, password).map {
            CategoryData(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId,
                streamType = StreamType.Series
            )
        }.apply {
            Logger.debug("fetchSeriesCategories: $this")
        }

    private suspend fun fetchSeries(category: CategoryData): List<SeriesStream> {
        val series = iptvService.getSeries(username, password, category.categoryId)
        Logger.debug("categoryId=${category.categoryId}, series.size=${series.size}")

        return series.map {
            SeriesStream(
                num = it.num,
                name = it.name,
                categoryId = it.categoryId,
                seriesId = it.seriesId,
                coverImageUrl = it.cover,
                plot = it.plot,
                cast = it.cast,
                director = it.director,
                genre = it.genre,
                releaseDate = it.releaseDate,
                lastModified = it.lastModified,
                rating = it.rating,
                backdropUrl = it.backdropPath.firstOrNull()
            )
        }
    }

    suspend fun getSeriesDataAndUpdate(streamId: Int) {
        try {
            iptvService.getSeriesDetails(seriesId = streamId).let { it ->
                val seasons = it.seasons.map { seasonData ->
                    Season(
                        episodes = it.episodes[seasonData.seasonNumber] ?: emptyList(),
                        number = seasonData.seasonNumber ?: -1,
                        name = seasonData.name ?: "",
                        coverImageUrl = seasonData.cover ?: "",
                        voteAverage = seasonData.voteAverage ?: 0f,
                        overview = seasonData.overview ?: "",
                    )
                }
                Logger.debug("seasons = [$seasons]")
                val filteredSeasons = seasons.filter { it.episodes.isNotEmpty() }
                seriesStreamsDao.getBySeriesId(streamId).copy(seasons = filteredSeasons).let {
                    seriesStreamsDao.update(it)
                }
            }
        } catch (e: Exception) {
            Logger.error(e.message.toString())
            e.printStackTrace()
        }
    }
}