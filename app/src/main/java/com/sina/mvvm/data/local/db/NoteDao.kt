package com.sina.mvvm.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.data.local.model.helper.SortBy
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    fun getNotes(search: String, isFavorite: Boolean, sortBy: SortBy) =
        when (sortBy) {
            SortBy.NAME -> getNotesByName(search, isFavorite)
            SortBy.DATE -> getNotesByDate(search, isFavorite)
        }

    @Query(
        "SELECT * FROM note_table WHERE isFavorite = CASE WHEN :favorite = 1 THEN 1 ELSE isFavorite END " +
                "AND (title LIKE '%' || :search || '%' OR description LIKE '%' || :search || '%' ) order by date desc"
    )
    fun getNotesByDate(search: String, favorite: Boolean): Flow<List<Note>>


    @Query(
        "SELECT * FROM note_table WHERE isFavorite = CASE WHEN :favorite = 1 THEN 1 ELSE isFavorite END " +
                "AND (title LIKE '%' || :search || '%' OR description LIKE '%' || :search || '%' ) order by title asc"
    )
    fun getNotesByName(search: String, favorite: Boolean): Flow<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("select exists(select 1 from note_table where id=:id limit 1)")
    suspend fun isNoteExist(id: Int): Boolean

}