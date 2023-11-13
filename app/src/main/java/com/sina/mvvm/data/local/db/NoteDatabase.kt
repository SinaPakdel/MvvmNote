package com.sina.mvvm.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.di.scope.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    class Callback @Inject constructor(
        private val database: Provider<NoteDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val noteDao = database.get().noteDao()
            applicationScope.launch {
                noteDao.insertNote(
                    Note(title = "1Test1", description = "Test1 Description", isFavorite = true)
                )
                noteDao.insertNote(
                    Note(title = "2Test2", description = "Test2 Description", isFavorite = false)
                )
                noteDao.insertNote(
                    Note(title = "3Test3", description = "Test3 Description", isFavorite = true)
                )
            }

        }
    }
}