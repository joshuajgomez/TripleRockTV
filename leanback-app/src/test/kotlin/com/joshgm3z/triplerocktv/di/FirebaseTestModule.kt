package com.joshgm3z.triplerocktv.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.joshgm3z.triplerocktv.FirebaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FirebaseModule::class]
)
class FirebaseTestModule {
    @Provides
    fun provideAnalytics(): FirebaseAnalytics {
        return mockk<FirebaseAnalytics>(relaxed = true)
    }

    @Provides
    fun provideFirestore(): FirebaseFirestore {
        return mockk<FirebaseFirestore>(relaxed = true)
    }
}