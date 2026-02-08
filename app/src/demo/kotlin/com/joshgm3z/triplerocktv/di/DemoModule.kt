package com.joshgm3z.triplerocktv.di

import com.joshgm3z.triplerocktv.impl.DemoLoginRepository
import com.joshgm3z.triplerocktv.impl.DemoMediaLocalRepository
import com.joshgm3z.triplerocktv.impl.DemoMediaOnlineRepository
import com.joshgm3z.triplerocktv.impl.DemoSubtitleRepositoryImpl
import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
import com.joshgm3z.triplerocktv.repository.SubtitleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoBindingModule {
    @Binds
    abstract fun bindLoginRepository(
        repo: DemoLoginRepository
    ): LoginRepository

    @Binds
    abstract fun bindMediaLocalRepository(
        repo: DemoMediaLocalRepository
    ): MediaLocalRepository

    @Binds
    abstract fun bindMediaOnlineRepository(
        repo: DemoMediaOnlineRepository
    ): MediaOnlineRepository

    @Binds
    abstract fun bindSubtitleRepository(
        repo: DemoSubtitleRepositoryImpl
    ): SubtitleRepository
}
