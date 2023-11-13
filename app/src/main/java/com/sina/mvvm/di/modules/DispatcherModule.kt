package com.sina.mvvm.di.modules

import com.sina.mvvm.di.scope.IODispatcherScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IODispatcherScope
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}