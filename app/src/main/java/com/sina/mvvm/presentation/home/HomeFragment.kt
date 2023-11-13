package com.sina.mvvm.presentation.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sina.mvvm.R
import com.sina.mvvm.data.local.model.helper.SortBy
import com.sina.mvvm.databinding.FragmentHomeBinding
import com.sina.mvvm.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var menuHost: MenuHost
    private lateinit var searchView: SearchView


    private val menuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_home, menu)
            implementSearchView(menu)
            implementFavoriteSate(menu)
        }

        private fun implementFavoriteSate(menu: Menu) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    viewModel.showFavorite.collectLatest {
                        menu.findItem(R.id.action_show_favorites).isChecked = it
                    }
                }
            }
        }

        private fun implementSearchView(menu: Menu) {
            val searchItem = menu.findItem(R.id.action_search)
            searchView = searchItem.actionView as SearchView
            val pendingQuery = viewModel.searchQuery.value
            if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                searchItem.expandActionView()
                searchView.setQuery(pendingQuery, false)
            }
            searchView.onQueryTextChanged {
                viewModel.searchQuery.value = it
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_show_favorites -> {
                    menuItem.isChecked = !menuItem.isChecked
                    viewModel.onFavoriteSelected(menuItem.isChecked)
                    true
                }

                R.id.action_sort_name -> {
                    viewModel.onSortBySelected(SortBy.NAME)
                    true
                }

                R.id.action_sort_date -> {
                    viewModel.onSortBySelected(SortBy.DATE)
                    true
                }

                else -> false
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        menuHost = requireActivity()

        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(binding.homeToolbar)
        }

        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewModel.getNotes()
        homeAdapter = HomeAdapter({ note ->
            viewModel.onItemClicked(note)
        }, { note, isFavorite ->
            viewModel.onFavoriteItemClicked(note, isFavorite)
        })
        binding.rvHome.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }

        observeEvents()

        binding.fabAddNote.setOnClickListener {
            viewModel.fabClicked()
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val note = homeAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onItemSwiped(note)
            }
        }).attachToRecyclerView(binding.rvHome)
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.homeEventChanel.collect { event ->
                    when (event) {
                        is HomeViewModel.HomeEvents.NavigateToNoteFragment ->
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeFragmentToNoteFragment(
                                    event.note
                                )
                            )

                        is HomeViewModel.HomeEvents.SendNotes -> homeAdapter.submitList(event.notes)
                        is HomeViewModel.HomeEvents.FabClicked ->
                            findNavController().navigate(
                                HomeFragmentDirections.actionHomeFragmentToNoteFragment(
                                    null
                                )
                            )

                        is HomeViewModel.HomeEvents.ShowUndoDeleteMessage -> {
                            Snackbar.make(requireView(),"Note Deleted" ,Snackbar.LENGTH_LONG)
                                .setAction("Undo"){
                                    viewModel.onUndoDeleteClicked(event.note)
                                }.show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchView.setOnQueryTextListener(null)
    }
}