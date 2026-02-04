package com.joshgm3z.triplerocktv.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joshgm3z.triplerocktv.repository.room.series.SeriesEntity
import com.joshgm3z.triplerocktv.repository.room.vod.StreamEntity
import com.joshgm3z.triplerocktv.repository.room.vod.StreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategory
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao

@Database(
    entities = [
        VodCategory::class,
        StreamEntity::class,
        SeriesEntity::class,
    ],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): VodCategoryDao
    abstract fun streamsDao(): StreamsDao
}
