package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategory
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao

@Database(
    entities = [
        StreamData::class,
        CategoryData::class,
        SeriesCategory::class,
        SeriesStream::class,
        IptvEpgListing::class,
    ],
    version = 14
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDataDao(): CategoryDataDao
    abstract fun streamDataDao(): StreamDataDao
    abstract fun seriesCategoryDao(): SeriesCategoryDao
    abstract fun seriesStreamsDao(): SeriesStreamsDao
    abstract fun epgListingDao(): EpgListingDao
}
