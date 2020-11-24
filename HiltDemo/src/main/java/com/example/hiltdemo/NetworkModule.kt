package com.example.hiltdemo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.OkHttpClient

@Module
@InstallIn(ActivityComponent::class)
class NetworkModule {

    @Provides
    @ActivityScoped
    fun providerOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }
}