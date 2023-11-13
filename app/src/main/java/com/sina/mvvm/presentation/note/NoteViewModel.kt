package com.sina.mvvm.presentation.note

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sina.mvvm.data.NoteRepository
import com.sina.mvvm.data.local.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val _noteEventChanel = Channel<NoteEvents>()
    val noteEventChannel = _noteEventChanel.receiveAsFlow()

    fun actionSaveClicked() {
        viewModelScope.launch {
            _noteEventChanel.send(NoteEvents.OnActionSaveClicked)
        }
    }

    fun saveNoteForMe(note: Note?) {
        viewModelScope.launch {
            if (note!=null) repository.saveNote(note)
        }
    }

    val note = state.get<Note>("note")

    sealed class NoteEvents {
        data object OnActionSaveClicked : NoteEvents()
    }
}