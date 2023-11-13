package com.sina.mvvm.presentation.note

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.sina.mvvm.R
import com.sina.mvvm.data.local.model.Note
import com.sina.mvvm.databinding.FragmentNoteBinding
import com.sina.mvvm.utils.toggleFavoriteIcon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_note) {
    private lateinit var binding: FragmentNoteBinding
    private val viewModel: NoteViewModel by viewModels()
    private var note: Note? = null
    private var isFavorite: Boolean = false
    private lateinit var menuHost: MenuHost


    private val menuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_note, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.actionSave -> {
                    viewModel.actionSaveClicked()
                    true
                }

                else -> false
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNoteBinding.bind(view)
        menuHost=requireActivity()
        val activity = requireActivity()
        if (activity is AppCompatActivity) activity.setSupportActionBar(binding.noteToolbar)

        if (viewModel.note != null) note = viewModel.note
        with(binding) {
            etTitle.setText(viewModel.note?.title)
            etDescription.setText(viewModel.note?.description)
            btnFavorite.toggleFavoriteIcon {
                isFavorite = it
            }
            viewModel.note?.isFavorite?.let { boolean ->
                btnFavorite.toggleFavoriteIcon(boolean) {
                    isFavorite = it
                }
            }
        }

        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.noteEventChannel.collect { event ->
                    when (event) {
                        NoteViewModel.NoteEvents.OnActionSaveClicked -> {
                            saveNote()
                        }
                    }
                }
            }
        }
    }

    private fun saveNote() {
        with(binding) {
            val title = etTitle.text?.toString().orEmpty()
            val description = etDescription.text?.toString().orEmpty()

            if (note != null) {
                note?.title = title
                note?.description = description
                note?.isFavorite = isFavorite
                viewModel.saveNoteForMe(note)
                findNavController().popBackStack()
            } else {
                if(title.isNotEmpty()){
                    viewModel.saveNoteForMe(
                        Note(
                            title = title,
                            description = description,
                            isFavorite = isFavorite
                        )
                    )
                    findNavController().popBackStack()
                }else Toast.makeText(requireContext(),"title must not be empty",Toast.LENGTH_SHORT).show()

            }
        }
    }
}