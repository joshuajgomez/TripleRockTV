package com.joshgm3z.triplerocktv

import com.joshgm3z.triplerocktv.repository.LoginRepository
import com.joshgm3z.triplerocktv.repository.MediaRepository
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
    abstract fun bindMediaRepository(
        repo: DemoMediaRepository
    ): MediaRepository
}
