package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.MediaLocalRepository
import com.joshgm3z.triplerocktv.repository.MediaOnlineRepository
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
}
