package com.joshgm3z.triplerocktv.repository.impl.helper

import com.joshgm3z.triplerocktv.repository.LoadingState
import com.joshgm3z.triplerocktv.repository.LoadingStatus
import com.joshgm3z.triplerocktv.repository.MediaLoadingType
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.LIMIT
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.password
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl.Companion.username
import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.util.Logger
import javax.inject.Inject

class SeriesFetcher
@Inject
constructor(
    private val iptvService: IptvService,
    private val seriesCategoryDao: SeriesCategoryDao,
    private val seriesStreamsDao: SeriesStreamsDao,
) {
    suspend fun fetchSeries(
        onFetch: (MediaLoadingType, LoadingState) -> Unit,
        onError: (String, String) -> Unit
    ) {
        Logger.entry()
        val categories = fetchSeriesCategories().subList(0, LIMIT)
        val total = categories.size
        if (total > 0) {
            seriesCategoryDao.deleteAllCategories()
            seriesStreamsDao.deleteAllStreams()
        } else {
            onError("Unable to fetch series categories.", "No categories found.")
            return
        }

        categories.forEachIndexed { index, it ->
            fetchAndStoreSeries(it)
            onFetch(
                MediaLoadingType.Series,
                LoadingState(
                    percent = (index.toFloat() / total * 100).toInt(),
                    status = LoadingStatus.Ongoing
                )
            )
        }
        onFetch(
            MediaLoadingType.Series,
            LoadingState(100, LoadingStatus.Complete)
        )
    }

    private suspend fun fetchSeriesCategories(): List<SeriesCategory> =
        iptvService.getSeriesCategories(username, password).map {
            SeriesCategory(
                categoryId = it.categoryId,
                categoryName = it.categoryName,
                parentId = it.parentId
            )
        }.apply {
            Logger.debug("fetchSeriesCategories: $this")
        }

    private suspend fun fetchAndStoreSeries(vodCategory: SeriesCategory) {
        val series = iptvService.getSeries(username, password, vodCategory.categoryId)
        Logger.debug("fetchAndStoreSeries: $series")

        seriesCategoryDao.insert(vodCategory.apply { count = series.size })
        seriesStreamsDao.insertStreams(series.map {
            SeriesStream(
                num = it.num,
                name = it.name,
                categoryId = it.categoryId,
                seriesId = it.seriesId,
                cover = it.cover,
                plot = it.plot,
                cast = it.cast,
                director = it.director,
                genre = it.genre,
                releaseDate = it.releaseDate,
                lastModified = it.lastModified,
                rating = it.rating
            )
        })
    }
}