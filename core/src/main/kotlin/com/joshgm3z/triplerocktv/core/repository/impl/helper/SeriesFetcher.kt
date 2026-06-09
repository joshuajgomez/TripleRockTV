package com.joshgm3z.triplerocktv.core.repository.impl.helper

import androidx.room.util.copy
import com.joshgm3z.triplerocktv.core.repository.LoadingState
import com.joshgm3z.triplerocktv.core.repository.LoadingStatus
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
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
import com.joshgm3z.triplerocktv.core.util.parseEpisodeNumber
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

        if (seriesStreamListToStore.isNotEmpty() && categoriesToStore.isNotEmpty()) {
            Logger.info("storing categories = [${categoriesToStore.size}], streams = [${seriesStreamListToStore.size}]")

            categoryDataDao.replaceData(StreamType.Series, categoriesToStore)
            seriesStreamsDao.replaceData(seriesStreamListToStore)
        }

        when {
            categoriesToStore.isEmpty() || seriesStreamListToStore.isEmpty() -> onFetch(
                LoadingState(
                    status = LoadingStatus.Error,
                    error = "Unable to update. Try again in a few min"
                )
            )

            errorMessage.isNotEmpty() -> onFetch(
                LoadingState(
                    status = LoadingStatus.Error,
                    error = "Unable to fully update. Try again in a few min"
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

    private suspend fun fetchSeriesCategories(): List<CategoryData> = try {
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
    } catch (e: Exception) {
        Logger.error(e.message.toString())
        e.printStackTrace()
        emptyList()
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
                val seasons = if (it.seasons.isNotEmpty()) it.seasons.map { seasonData ->
                    Season(
                        episodes = it.episodes[seasonData.seasonNumber] ?: emptyList(),
                        number = seasonData.seasonNumber ?: -1,
                        name = seasonData.name ?: "",
                        coverImageUrl = seasonData.cover ?: "",
                        voteAverage = seasonData.voteAverage ?: 0f,
                        overview = seasonData.overview ?: "",
                    )
                } else if (it.episodes.isNotEmpty()) {
                    it.episodes.keys.map { seasonNumber ->
                        Season(
                            episodes = it.episodes[seasonNumber]?.fixEpisodeNumbers()
                                ?: emptyList(),
                            number = seasonNumber,
                            name = "Season $seasonNumber",
                            coverImageUrl = "",
                            voteAverage = 0f,
                            overview = "",
                        )
                    }
                } else emptyList()
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

private fun List<Episode>.fixEpisodeNumbers(): List<Episode> {
    return this.map { episode ->
        episode.copy(
            episode_num = episode.title.parseEpisodeNumber(episode.episode_num)
        )
    }
}
