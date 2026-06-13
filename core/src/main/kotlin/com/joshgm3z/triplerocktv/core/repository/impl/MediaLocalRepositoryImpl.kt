package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.MIN_DURATION_LEFT
import com.joshgm3z.triplerocktv.core.repository.room.stream.MIN_PLAYBACK_DURATION
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.favorite.Favorite
import com.joshgm3z.triplerocktv.core.repository.room.favorite.FavoriteDao
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayedDao
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MissingSeasonsException(
    message: String = "Missing seasons. Cannot fetch last played episode"
) : Exception(message)

class MediaLocalRepositoryImpl @Inject constructor(
    private val epgListingDao: EpgListingDao,
    private val seriesStreamsDao: SeriesStreamsDao,
    private val streamDataDao: StreamDataDao,
    private val categoryDataDao: CategoryDataDao,
    private val favoriteDao: FavoriteDao,
    private val recentlyPlayedDao: RecentlyPlayedDao,
) : MediaLocalRepository {

    override suspend fun fetchCategories(
        streamType: StreamType
    ): List<CategoryData> = categoryDataDao.getAllOfType(streamType)

    override suspend fun fetchCategoriesByTitleKey(
        streamType: StreamType,
        titleKey: String
    ): List<CategoryData> {
        return categoryDataDao.getAllOfTypeWithTitleKey(streamType, titleKey)
    }

    override suspend fun fetchEpgListings(): List<IptvEpgListing> =
        epgListingDao.getAllEpgListings()

    override suspend fun fetchStreamsOfCategory(
        categoryId: Int,
        streamType: StreamType
    ): List<Any> = when (streamType) {
        StreamType.Series -> seriesStreamsDao.getAllOfCategory(categoryId)
        else -> streamDataDao.getAllFromCategoryAndType(categoryId, streamType)
    }.apply {
        Logger.info("fetchStreamsOfCategory($categoryId, $streamType): $this")
    }

    override suspend fun fetchStream(
        streamId: Int,
        streamType: StreamType,
    ): StreamData = streamDataDao.getByStreamId(streamId).apply {
        inMyList = favoriteDao.isFavorite(streamId).first()
        recentlyPlayed = recentlyPlayedDao.getRecentlyPlayedById(streamId).first()
    }

    override suspend fun fetchEpisode(
        episodeId: Int,
        seriesId: Int,
    ): Episode {
        val seriesStream = seriesStreamsDao.getBySeriesId(seriesId)
        seriesStream.seasons?.forEach { season ->
            season.episodes.forEach { episode ->
                if (episode.id == episodeId) return episode.apply {
                    recentlyPlayed = recentlyPlayedDao.getRecentlyPlayedById(episodeId).first()
                }
            }
        }
        throw Exception("Episode $episodeId for series $seriesId not found")
    }

    override suspend fun streamDataFlow(
        streamId: Int,
        streamType: StreamType,
    ): Flow<StreamData> = when (streamType) {
        StreamType.VideoOnDemand -> combine(
            streamDataDao.streamDataFlow(streamId),
            recentlyPlayedDao.getRecentlyPlayedById(streamId),
            favoriteDao.isFavorite(streamId)
        ) { streamData, recentPlayed, isFavorite ->
            Logger.debug("combine: recentPlayed = [${recentPlayed}], isFavorite = [${isFavorite}]")
            streamData.apply {
                recentlyPlayed = recentPlayed
                inMyList = isFavorite
            }
        }

        else -> streamDataDao.streamDataFlow(streamId)
    }

    override fun seriesStreamFlow(seriesId: Int): Flow<SeriesStream> = combine(
        seriesStreamsDao.seriesStreamFlow(seriesId),
        recentlyPlayedDao.getRecentlyPlayedBySeriesId(seriesId),
        favoriteDao.isFavorite(seriesId)
    ) { seriesStream, recentPlayed, isFavorite ->
        seriesStream.apply {
            inMyList = isFavorite
            recentPlayed?.let { lastPlayedEpisodeId = it.id }
            seasons?.forEach { season ->
                season.episodes.forEach { episode ->
                    episode.recentlyPlayed = recentlyPlayedDao
                        .getRecentlyPlayedById(episode.id)
                        .first()
                }
            }
        }
    }

    override suspend fun isContentEmpty(): Boolean = categoryDataDao.getAll().isEmpty()
            && epgListingDao.getAllEpgListings().isEmpty()

    override suspend fun fetchMyList(streamType: StreamType): List<StreamData> {
        return favoriteDao.getFavoritesOfType(streamType).map {
            streamDataDao.getByStreamId(it.id)
        }
    }

    override suspend fun fetchNewlyAdded(streamType: StreamType): List<StreamData> {
        return if (streamType == StreamType.VideoOnDemand) streamDataDao.getNewlyAdded10()
        else emptyList()
    }

    override suspend fun fetchMyListSeries(): List<SeriesStream> {
        return favoriteDao.getFavoritesOfType(StreamType.Series).map {
            seriesStreamsDao.getBySeriesId(it.id)
        }
    }

    override suspend fun updateMyList(
        streamId: Int,
        streamType: StreamType,
        add: Boolean
    ) = if (add) favoriteDao.insert(
        Favorite(
            streamId,
            streamType,
            System.currentTimeMillis()
        )
    )
    else favoriteDao.delete(streamId)

    override suspend fun updateSelectedSubtitle(
        streamId: Int,
        language: String,
        title: String,
        url: String?
    ) {
        streamDataDao.updateSubtitleLanguage(streamId, language, title)
        url?.let {
            streamDataDao.updateSubtitleUrl(streamId, it)
        }
    }

    override suspend fun numberOfFiles(type: StreamType): Int {
        return when (type) {
            StreamType.VideoOnDemand,
            StreamType.LiveTV -> streamDataDao.getAll(type).size

            StreamType.Series -> seriesStreamsDao.getAll().size
        }
    }
}
