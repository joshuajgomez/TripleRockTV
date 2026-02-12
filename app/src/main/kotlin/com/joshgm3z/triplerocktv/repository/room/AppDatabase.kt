package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategory
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStream
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodStream
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao

@Database(
    entities = [
        VodCategory::class,
        VodStream::class,
        SeriesCategory::class,
        SeriesStream::class,
        LiveTvCategory::class,
        LiveTvStream::class,
        IptvEpgListing::class,
    ],
    version = 13
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vodCategoryDao(): VodCategoryDao
    abstract fun vodStreamsDao(): VodStreamsDao
    abstract fun seriesCategoryDao(): SeriesCategoryDao
    abstract fun seriesStreamsDao(): SeriesStreamsDao
    abstract fun liveTvCategoryDao(): LiveTvCategoryDao
    abstract fun liveTvStreamsDao(): LiveTvStreamsDao
    abstract fun epgListingDao(): EpgListingDao
}
