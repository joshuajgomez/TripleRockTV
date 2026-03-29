package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.core.repository.LoginRepository
import com.joshgm3z.triplerocktv.core.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.core.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.core.repository.SearchRepository
import com.joshgm3z.triplerocktv.core.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.core.repository.impl.LoginRepositoryImpl
import com.joshgm3z.triplerocktv.core.repository.impl.MediaLocalRepositoryImpl
import com.joshgm3z.triplerocktv.core.repository.impl.MediaOnlineRepositoryImpl
import com.joshgm3z.triplerocktv.core.repository.impl.SearchRepositoryImpl
import com.joshgm3z.triplerocktv.core.repository.impl.SubtitleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OnlineBindingModule {
    @Binds
    abstract fun bindLoginRepository(
        repo: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    abstract fun bindMediaLocalRepository(
        repo: MediaLocalRepositoryImpl
    ): MediaLocalRepository

    @Binds
    abstract fun bindSearchRepository(
        repo: SearchRepositoryImpl
    ): SearchRepository

    @Binds
    abstract fun bindMediaOnlineRepository(
        repo: MediaOnlineRepositoryImpl
    ): MediaOnlineRepository

    @Binds
    abstract fun bindSubtitleRepository(
        repo: SubtitleRepositoryImpl
    ): SubtitleRepository
}
