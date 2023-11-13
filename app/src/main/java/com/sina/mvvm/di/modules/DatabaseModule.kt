package com.sina.mvvm.di.modules

import android.app.Application
import androidx.room.Room
import com.sina.mvvm.data.NoteRepository
import com.sina.mvvm.data.local.LocalDataSource
import com.sina.mvvm.data.local.LocalDataSourceImpl
import com.sina.mvvm.data.local.db.NoteDao
import com.sina.mvvm.data.local.db.NoteDatabase
import com.sina.mvvm.di.scope.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: NoteDatabase.Callback): NoteDatabase =
        Room.databaseBuilder(application, NoteDatabase::class.java, "mvvm_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun provideNoteDao(noteDatabase: NoteDatabase) = noteDatabase.noteDao()

    @Provides
    fun provideLocalDataSource(noteDao: NoteDao) = LocalDataSourceImpl(noteDao)

    @Provides
    fun provideRepository(localDataSource: LocalDataSourceImpl) = NoteRepository(localDataSource)

    @Singleton
    @Provides
    @ApplicationScope
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}