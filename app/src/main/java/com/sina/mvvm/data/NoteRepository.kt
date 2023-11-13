package com.sina.mvvm.data

import com.sina.mvvm.data.local.LocalDataSource
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.data.local.model.helper.SortBy
import com.sina.mvvm.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource) {

    fun getNotes(search: String, isFavorite: Boolean, sortBy: SortBy): Flow<List<Note>> {
        return localDataSource.getNotes(search, isFavorite, sortBy)
    }

    suspend fun saveNote(note: Note) {
        if (note.id == 0) localDataSource.insetNote(note)
        else if (localDataSource.isNoteExist(note.id)) localDataSource.updateNote(note)
        else localDataSource.insetNote(note)
    }

    suspend fun deleteNote(note: Note) = localDataSource.deleteNote(note)

}