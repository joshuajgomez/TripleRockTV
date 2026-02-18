package com.joshgm3z.triplerocktv.di

import android.content.Context
import androidx.room.Room
import com.joshgm3z.triplerocktv.repository.room.AppDatabase
import com.joshgm3z.triplerocktv.repository.room.CategoryDataDao
import com.joshgm3z.triplerocktv.repository.room.StreamDataDao
import com.joshgm3z.triplerocktv.repository.room.epg.EpgListingDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesCategoryDao
import com.joshgm3z.triplerocktv.repository.room.series.SeriesStreamsDao
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
    fun provideCategoryDataDao(appDatabase: AppDatabase): CategoryDataDao {
        return appDatabase.categoryDataDao()
    }

    @Provides
    fun provideStreamDataDao(appDatabase: AppDatabase): StreamDataDao {
        return appDatabase.streamDataDao()
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
    fun provideEpgListingDao(appDatabase: AppDatabase): EpgListingDao {
        return appDatabase.epgListingDao()
    }
}
