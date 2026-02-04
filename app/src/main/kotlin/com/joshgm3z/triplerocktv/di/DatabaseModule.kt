package com.joshgm3z.triplerocktv.di

import android.content.Context
import androidx.room.Room
import com.joshgm3z.triplerocktv.repository.room.AppDatabase
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvCategoryDao
import com.joshgm3z.triplerocktv.repository.room.live.LiveTvStreamsDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodCategoryDao
import com.joshgm3z.triplerocktv.repository.room.vod.VodStreamsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "triple_rock_tv_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideVodCategoryDao(appDatabase: AppDatabase): VodCategoryDao {
        return appDatabase.vodCategoryDao()
    }

    @Provides
    fun provideVodStreamDao(appDatabase: AppDatabase): VodStreamsDao {
        return appDatabase.vodStreamsDao()
    }

    @Provides
    fun provideSeriesCategoryDao(appDatabase: AppDatabase): SeriesCategoryDao {
        return appDatabase.seriesCategoryDao()
    }

    @Provides
    fun provideSeriesStreamDao(appDatabase: AppDatabase): SeriesStreamsDao {
        return appDatabase.seriesStreamsDao()
    }

    @Provides
    fun provideLiveTvCategoryDao(appDatabase: AppDatabase): LiveTvCategoryDao {
        return appDatabase.liveTvCategoryDao()
    }

    @Provides
    fun provideLiveTvStreamDao(appDatabase: AppDatabase): LiveTvStreamsDao {
        return appDatabase.liveTvStreamsDao()
    }

    @Provides
    fun provideEpgListingDao(appDatabase: AppDatabase): EpgListingDao {
        return appDatabase.epgListingDao()
    }
}
