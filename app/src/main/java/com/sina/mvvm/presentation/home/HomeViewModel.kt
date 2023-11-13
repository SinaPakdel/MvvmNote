package com.sina.mvvm.presentation.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sina.mvvm.data.NoteRepository
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.data.local.model.helper.SortBy
import com.sina.mvvm.data.local.prefs.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val state: SavedStateHandle,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _homeEventChanel = Channel<HomeEvents>()
    val homeEventChanel = _homeEventChanel.receiveAsFlow()

    private val _showFavorite = MutableStateFlow(false)
    val showFavorite: StateFlow<Boolean> = _showFavorite

    val searchQuery = state.getLiveData("search", "")

    private val notes = combine(
        searchQuery.asFlow(), prefManager.readSearchNote
    ) { query, filters -> Pair(query, filters) }.flatMapLatest { (query, filters) ->
        repository.getNotes(query, filters.isFavorite, filters.sortBy)
    }


    init {
        giveMeFavoriteStateAction()
    }

    private fun giveMeFavoriteStateAction() {
        viewModelScope.launch {
            prefManager.readFavorite.collectLatest {
                _showFavorite.emit(it)
            }
        }
    }

    fun fabClicked() {
        viewModelScope.launch {
            _homeEventChanel.send(HomeEvents.FabClicked)
        }
    }

    fun getNotes() {
        viewModelScope.launch {
            notes.collectLatest {
                _homeEventChanel.send(HomeEvents.SendNotes(it))
            }
        }
    }

    fun onFavoriteSelected(favorite: Boolean) {
        viewModelScope.launch { prefManager.saveFavorite(favorite) }
    }

    fun onSortBySelected(sort: SortBy) {
        viewModelScope.launch { prefManager.saveSortOrder(sort) }
    }

    fun onItemClicked(note: Note) {
        viewModelScope.launch {
            _homeEventChanel.send(HomeEvents.NavigateToNoteFragment(note))
        }
    }

    fun onFavoriteItemClicked(note: Note, favorite: Boolean) = viewModelScope.launch {
        repository.saveNote(note.copy(isFavorite = favorite))
    }

    fun onItemSwiped(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
        _homeEventChanel.send(HomeEvents.ShowUndoDeleteMessage(note))
    }

    fun onUndoDeleteClicked(note: Note) = viewModelScope.launch {
        repository.saveNote(note)
    }

    sealed class HomeEvents {
        data object FabClicked : HomeEvents()
        data class NavigateToNoteFragment(val note: Note) : HomeEvents()
        data class SendNotes(val notes: List<Note>) : HomeEvents()
        data class ShowUndoDeleteMessage(val note: Note) : HomeEvents()
    }
}
