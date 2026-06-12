package com.joshgm3z.triplerocktv.core.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryData
import com.joshgm3z.triplerocktv.core.repository.room.category.CategoryDataDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.core.repository.room.epg.IptvEpgListing
import com.joshgm3z.triplerocktv.core.repository.room.favorite.Favorite
import com.joshgm3z.triplerocktv.core.repository.room.favorite.FavoriteDao
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayed
import com.joshgm3z.triplerocktv.core.repository.room.recentlyplayed.RecentlyPlayedDao
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStream
import com.joshgm3z.triplerocktv.core.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamData
import com.joshgm3z.triplerocktv.core.repository.room.stream.StreamDataDao

@Database(
    entities = [
        StreamData::class,
        CategoryData::class,
        SeriesStream::class,
        IptvEpgListing::class,
        SearchHint::class,
        Favorite::class,
        RecentlyPlayed::class,
    ],
    version = 30
)
@TypeConverters(SeasonConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDataDao(): CategoryDataDao
    abstract fun streamDataDao(): StreamDataDao
    abstract fun seriesStreamsDao(): SeriesStreamsDao
    abstract fun epgListingDao(): EpgListingDao
    abstract fun searchHintDao(): SearchHintDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
}
