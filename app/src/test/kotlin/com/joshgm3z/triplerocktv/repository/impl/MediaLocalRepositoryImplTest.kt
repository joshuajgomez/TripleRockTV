package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.data.EpisodeInfo
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.series.Season
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals

class MediaLocalRepositoryImplTest {
    private lateinit var repository: MediaLocalRepository

    @MockK(relaxed = true)
    lateinit var epgListingDao: EpgListingDao

    @MockK(relaxed = true)
    lateinit var seriesStreamsDao: SeriesStreamsDao

    @MockK(relaxed = true)
    lateinit var streamDataDao: StreamDataDao

    @MockK(relaxed = true)
    lateinit var categoryDataDao: CategoryDataDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repository = MediaLocalRepositoryImpl(
            epgListingDao,
            seriesStreamsDao,
            streamDataDao,
            categoryDataDao
        )
    }

    @Test
    fun `Verify updateLastPlayedTimestamp updates episode in series`() = runTest {
        // mock
        val episodeId = 2
        val streamType = StreamType.Series
        val sampleSeriesData = sampleSeriesData()
        coEvery { seriesStreamsDao.getBySeriesId(episodeId) } returns sampleSeriesData

        val seriesStreamSlot = slot<SeriesStream>()
        coJustRun { seriesStreamsDao.update(capture(seriesStreamSlot)) }

        fun SeriesStream.assertEpisode(
            streamId: Int,
            doAssert: (Episode) -> Unit
        ) = seasons?.forEach { season ->
            season.episodes.forEach { episode ->
                if (episode.id == streamId) doAssert(episode)
            }
        }

        // should be 0L initially
        sampleSeriesData.assertEpisode(episodeId) { assertEquals(0L, it.lastPlayed) }
        sampleSeriesData.assertEpisode(1) { assertEquals(0L, it.lastPlayed) }

        // method call
        repository.updateLastPlayedTimestamp(episodeId, streamType, 5L)

        // verify
        assert(seriesStreamSlot.isCaptured)
        seriesStreamSlot.captured.let {
            // should be untouched
            it.assertEpisode(1) { assertEquals(0L, it.lastPlayed) }
            // should be changed
            it.assertEpisode(episodeId) { assertEquals(5L, it.lastPlayed) }

        }
    }

    fun sampleSeriesData() = SeriesStream(
        seasons = listOf(
            Season(
                episodes = listOf(
                    Episode(
                        id = 1,
                        episode_num = 1,
                        title = "Pilot",
                        container_extension = "mp4",
                        season = 1,
                        added = "2024-01-01",
                        episodeInfo = EpisodeInfo(
                            releasedate = "2024-01-01",
                            plot = 101,
                            duration_secs = 3600,
                            duration = "60m",
                            movie_image = null,
                            rating = "8.0",
                            season = "1"
                        ),
                        lastPlayed = 0,
                        playedDuration = 0
                    )
                ),
                number = 1,
                name = "Season 1",
                coverImageUrl = "https://example.com/season1.jpg",
                voteAverage = 8.0f
            ),
            Season(
                episodes = listOf(
                    Episode(
                        id = 2,
                        episode_num = 1,
                        title = "New Beginnings",
                        container_extension = "mkv",
                        season = 2,
                        added = "2025-01-01",
                        episodeInfo = EpisodeInfo(
                            releasedate = "2025-01-01",
                            plot = 102,
                            duration_secs = 3700,
                            duration = "62m",
                            movie_image = null,
                            rating = "8.5",
                            season = "2"
                        ),
                        lastPlayed = 0,
                        playedDuration = 0
                    )
                ),
                number = 2,
                name = "Season 2",
                coverImageUrl = "https://example.com/season2.jpg",
                voteAverage = 0.5f
            )
        ),
        seriesId = 11,
        categoryId = 11,
        backdropUrl = null,
        cast = null,
        coverImageUrl = null,
        director = null,
        genre = null,
        lastModified = null,
        name = "Sample Series",
        num = 1,
        plot = null,
        rating = null,
        releaseDate = null
    )

}