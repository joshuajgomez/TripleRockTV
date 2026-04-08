package com.joshgm3z.triplerocktv.di

import com.joshgm3z.triplerocktv.core.repository.AccessControlRepository
import com.joshgm3z.triplerocktv.impl.DemoLoginRepositoryImpl
import com.joshgm3z.triplerocktv.impl.DemoMediaLocalRepositoryImpl
import com.joshgm3z.triplerocktv.impl.DemoMediaOnlineRepositoryImpl
import com.joshgm3z.triplerocktv.impl.DemoSearchRepositoryImpl
import com.joshgm3z.triplerocktv.impl.DemoSubtitleRepositoryImpl
import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.impl.DemoAccessControlRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoBindingModule {
    @Binds
    abstract fun bindLoginRepository(
        repo: DemoLoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindMediaLocalRepository(
        repo: DemoMediaLocalRepositoryImpl
    ): MediaLocalRepository

    @Binds
    abstract fun bindMediaOnlineRepository(
        repo: DemoMediaOnlineRepositoryImpl
    ): MediaOnlineRepository

    @Binds
    abstract fun bindSubtitleRepository(
        repo: DemoSubtitleRepositoryImpl
    ): SubtitleRepository

    @Binds
    abstract fun bindSearchRepository(
        repo: DemoSearchRepositoryImpl
    ): SearchRepository

    @Binds
    abstract fun bindAccessControlRepository(
        repo: DemoAccessControlRepositoryImpl
    ): AccessControlRepository
}
