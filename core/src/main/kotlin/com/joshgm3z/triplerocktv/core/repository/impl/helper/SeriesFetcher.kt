package com.joshgm3z.triplerocktv.core.repository.impl.helper

import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.core.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.series.Season
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.util.Logger
import com.joshgm3z.triplerocktv.core.util.parseEpisodeNumber
import javax.inject.Inject

class SeriesFetcher
@Inject
constructor(
    private val categoryDataDao: CategoryDataDao,
    private val seriesStreamsDao: SeriesStreamsDao,
) {
    lateinit var iptvService: IptvService

    suspend fun fetchContent() {
        Logger.entry()
        val categories = fetchSeriesCategories()
        if (categories.isNotEmpty()) {
            Logger.info("Updating categories = [${categories.size}]")
            categoryDataDao.replaceData(StreamType.Series, categories)
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

    suspend fun updateSeriesOfCategory(categoryId: Int) {
        val series = iptvService.getSeries(username, password, categoryId)
        Logger.debug("categoryId=${categoryId}, series.size=${series.size}")

        val seriesStreams = series.map {
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
        seriesStreamsDao.replaceSeriesOfCategory(categoryId, seriesStreams)
    }

    suspend fun getSeriesDataAndUpdate(streamId: Int) {
        try {
            iptvService.getSeriesDetails(seriesId = streamId).let { it ->
                val seasons = if (it.seasons.isNotEmpty()) it.seasons.map { seasonData ->
                    Season(
                        episodes = it.episodes[seasonData.seasonNumber]
                            ?.sortedBy { it.episode_num }
                            ?: emptyList(),
                        number = seasonData.seasonNumber ?: -1,
                        name = seasonData.name ?: "",
                        coverImageUrl = seasonData.cover ?: "",
                        voteAverage = seasonData.voteAverage ?: 0f,
                        overview = seasonData.overview ?: "",
                    )
                } else if (it.episodes.isNotEmpty()) {
                    it.episodes.keys.map { seasonNumber ->
                        Season(
                            episodes = it.episodes[seasonNumber]
                                ?.fixEpisodeNumbers()
                                ?.sortedBy { it.episode_num }
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
                val seriesStream = seriesStreamsDao.getBySeriesId(streamId) ?: return@let
                seriesStream.copy(seasons = filteredSeasons).let {
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
            episode_num = episode.title.parseEpisodeNumber(episode.episode_num),
            title = episode.title.trim().removeSuffix("-").trim()
        )
    }
}
