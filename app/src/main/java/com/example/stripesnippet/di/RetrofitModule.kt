package com.example.stripesnippet.di

import com.example.stripesnippet.network.StripeBackendService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Singleton
    @Provides
    fun provideStripeBackendService(): StripeBackendService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://us-central1-stripesnippetbackend.cloudfunctions.net")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        return retrofit.create(StripeBackendService::class.java)
    }
}