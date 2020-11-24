package com.example.hiltdemo

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Qualifier

// 提供依赖注入的模块
@Module
// activity范围内可用
@InstallIn(ActivityComponent::class)
abstract class EngineModule {

    // 使用此注解提供实例
    @Binds
    @BindGasEngine
    abstract fun bindGasEngine(gasEngine: GasEngine): Engine

    @Binds
    @BindElectricEngine
    abstract fun bindElectricEngine(electricEngine: ElectricEngine): Engine
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BindGasEngine

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BindElectricEngine