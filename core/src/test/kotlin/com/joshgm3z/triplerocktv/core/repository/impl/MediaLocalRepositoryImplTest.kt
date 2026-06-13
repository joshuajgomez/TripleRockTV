package com.joshgm3z.triplerocktv.core.repository.impl

import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.StreamType
import com.joshgm3z.triplerocktv.core.repository.data.Episode
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.core.repository.room.favorite.FavoriteDao
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayedDao
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coJustRun
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

    @MockK(relaxed = true)
    lateinit var favoriteDao: FavoriteDao

    @MockK(relaxed = true)
    lateinit var recentlyPlayedDao: RecentlyPlayedDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        repository = MediaLocalRepositoryImpl(
            epgListingDao,
            seriesStreamsDao,
            streamDataDao,
            categoryDataDao,
            favoriteDao,
            recentlyPlayedDao
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
//        sampleSeriesData.assertEpisode(episodeId) { assertEquals(0L, it.lastPlayed) }
//        sampleSeriesData.assertEpisode(1) { assertEquals(0L, it.lastPlayed) }

        // method call
//        repository.updateEpisodeLastPlayedTimestamp(episodeId, 5)

        // verify
        assert(seriesStreamSlot.isCaptured)
        seriesStreamSlot.captured.let {
            // should be untouched
//            it.assertEpisode(1) { assertEquals(0L, it.lastPlayed) }
            // should be changed
//            it.assertEpisode(episodeId) { assertEquals(5L, it.lastPlayed) }

        }
    }


}
