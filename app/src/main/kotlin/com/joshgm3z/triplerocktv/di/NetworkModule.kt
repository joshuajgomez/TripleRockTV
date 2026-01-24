package com.joshgm3z.triplerocktv.di

import com.joshgm3z.triplerocktv.repository.retrofit.IptvService
import com.joshgm3z.triplerocktv.repository.retrofit.SampleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://webhop.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSampleService(retrofit: Retrofit): SampleService {
        return retrofit.create(SampleService::class.java)
    }

    @Provides
    @Singleton
    fun provideIptvService(retrofit: Retrofit): IptvService {
        return retrofit.create(IptvService::class.java)
    }
}
