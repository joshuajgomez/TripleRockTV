package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.CategoryData
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.series.Season
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
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
        onFetch: (StreamType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val categories = fetchSeriesCategories().let {
            if (limit != null) it.subList(0, limit) else it
        }
        val total = categories.size
        if (total > 0) {
            categoryDataDao.deleteAllOfType(StreamType.Series)
            seriesStreamsDao.deleteAllStreams()
        } else {
            onFetch(
                StreamType.Series,
                LoadingState(0, LoadingStatus.Error)
            )
            return
        }

        categories.forEachIndexed { index, it ->
            fetchAndStoreSeries(it)
            onFetch(
                StreamType.Series,
                LoadingState(
                    percent = (index.toFloat() / total * 100).toInt(),
                    status = LoadingStatus.Ongoing
                )
            )
        }
        onFetch(
            StreamType.Series,
            LoadingState(100, LoadingStatus.Complete)
        )
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

    private suspend fun fetchAndStoreSeries(category: CategoryData) {
        val series = iptvService.getSeries(username, password, category.categoryId)
        Logger.debug("fetchAndStoreSeries: $series")

        categoryDataDao.insert(category.apply {
            count = series.size
            firstStreamIcon = series.firstOrNull()?.cover
        })
        seriesStreamsDao.insertStreams(series.map {
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
        })
    }

    suspend fun getSeriesDataAndUpdate(streamId: Int) {
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
    }
}