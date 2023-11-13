package com.sina.mvvm.data.local

import com.sina.mvvm.data.local.db.NoteDao
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.data.local.model.helper.SortBy
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val noteDao: NoteDao) : LocalDataSource {
    override fun getNotes(search: String, isFavorite: Boolean, sortBy: SortBy): Flow<List<Note>> =
        noteDao.getNotes(search, isFavorite, sortBy)


    override suspend fun insetNote(note: Note) = noteDao.insertNote(note)

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    override suspend fun isNoteExist(id: Int): Boolean = noteDao.isNoteExist(id)
}