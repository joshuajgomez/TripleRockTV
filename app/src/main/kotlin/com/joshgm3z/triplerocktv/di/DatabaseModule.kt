package com.joshgm3z.triplerocktv.di

import android.content.Context
import androidx.room.Room
import com.joshgm3z.triplerocktv.repository.room.AppDatabase
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
    fun provideCategoryDao(appDatabase: AppDatabase): VodCategoryDao {
        return appDatabase.vodCategoryDao()
    }

    @Provides
    fun provideStreamDao(appDatabase: AppDatabase): VodStreamsDao {
        return appDatabase.vodStreamsDao()
    }
}
