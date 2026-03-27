package com.joshgm3z.triplerocktv.repository.impl

import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.StreamType
import com.joshgm3z.triplerocktv.repository.data.Episode
import com.joshgm3z.triplerocktv.repository.data.EpisodeInfo
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamData
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

//    @Test
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
        repository.updateEpisodeLastPlayedTimestamp(episodeId, 5)

        // verify
        assert(seriesStreamSlot.isCaptured)
        seriesStreamSlot.captured.let {
            // should be untouched
            it.assertEpisode(1) { assertEquals(0L, it.lastPlayed) }
            // should be changed
            it.assertEpisode(episodeId) { assertEquals(5L, it.lastPlayed) }

        }
    }

    @Test
    fun `Verify fetchRecentlyPlayed returns movies and series`() = runTest {
        coEvery { streamDataDao.getLastPlayed10() } returns sampleStreamData()
        coEvery { seriesStreamsDao.getLastPlayed10() } returns listOf(sampleSeriesData())

        repository.fetchRecentlyPlayedStreamData().let { it ->
            it.forEach {
                println("${it.name} last played ${it.lastPlayed}")
            }
        }
    }

}
