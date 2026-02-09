package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import com.joshgm3z.triplerocktv.repository.impl.LoginRepositoryImpl
import com.joshgm3z.triplerocktv.repository.impl.MediaLocalRepositoryImpl
import com.joshgm3z.triplerocktv.repository.impl.MediaOnlineRepositoryImpl
import com.joshgm3z.triplerocktv.repository.impl.SubtitleRepositoryImpl
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
    abstract fun bindMediaOnlineRepository(
        repo: MediaOnlineRepositoryImpl
    ): MediaOnlineRepository

    @Binds
    abstract fun bindSubtitleRepository(
        repo: SubtitleRepositoryImpl
    ): SubtitleRepository
}
