package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
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
    ],
    version = 9
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vodCategoryDao(): VodCategoryDao
    abstract fun vodStreamsDao(): VodStreamsDao
    abstract fun seriesCategoryDao(): SeriesCategoryDao
}
