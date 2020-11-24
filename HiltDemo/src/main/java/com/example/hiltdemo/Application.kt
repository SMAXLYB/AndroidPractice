package com.example.hiltdemo

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
class Application {

    // 可以向下转型获得自定义的application
    @Provides
    fun providerMyApplication(application: Application): MyApplication {
        return application as MyApplication
    }
}