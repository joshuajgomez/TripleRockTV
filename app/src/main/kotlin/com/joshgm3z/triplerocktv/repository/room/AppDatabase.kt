package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao

@Database(
    entities = [
        StreamData::class,
        CategoryData::class,
        SeriesStream::class,
        IptvEpgListing::class,
        SearchHint::class,
    ],
    version = 27
)
@TypeConverters(SeasonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDataDao(): CategoryDataDao
    abstract fun streamDataDao(): StreamDataDao
    abstract fun seriesStreamsDao(): SeriesStreamsDao
    abstract fun epgListingDao(): EpgListingDao
    abstract fun searchHintDao(): SearchHintDao
}
