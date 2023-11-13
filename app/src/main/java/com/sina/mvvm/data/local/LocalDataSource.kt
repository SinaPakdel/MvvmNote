package com.sina.mvvm.data.local

import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.data.local.model.helper.SortBy
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getNotes(search: String, isFavorite: Boolean, sortBy: SortBy): Flow<List<Note>>
    suspend fun insetNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun isNoteExist(id: Int): Boolean
}