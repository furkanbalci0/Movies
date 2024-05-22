package com.furkanbalci.movie.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.furkanbalci.movie.R
import com.furkanbalci.movie.databinding.FragmentFavoritesBinding
import com.furkanbalci.movie.ui.adapter.MoviesAdapter
import com.furkanbalci.movie.ui.adapter.SpaceItemDecoration
import com.furkanbalci.movie.ui.viewmodel.SharedViewModel
import com.furkanbalci.movie.util.Constants
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private val sharedViewModel: SharedViewModel by viewModel()
    private val favoritesViewModel: FavoritesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeFavorites()
    }

    private fun observeFavorites() {

        favoritesViewModel.getFavorites()

        lifecycleScope.launch {
            favoritesViewModel.favoritesMoviesLiveData.observe(viewLifecycleOwner) {
                moviesAdapter.list = it.toMutableList()
            }
        }

        lifecycleScope.launch {
            sharedViewModel.updateMovieLiveData.observe(viewLifecycleOwner) {
                moviesAdapter.removeItem(it)
            }
        }
    }

    private fun initRecyclerView() {
        moviesAdapter = MoviesAdapter(
            onItemClick = { movieModel ->
                findNavController().navigate(R.id.action_favoritesFragment_to_detailFragment, Bundle().apply {
                    putInt(Constants.MOVIE_ID_KEY, movieModel.id)
                })
            },
            onStarClick = { movieModel ->
                sharedViewModel.setFavoriteItem(movieModel)
            })

        binding.apply {
            popularMoviesRecyclerView.adapter = moviesAdapter
            val spaceDecoration = SpaceItemDecoration(16)
            popularMoviesRecyclerView.addItemDecoration(spaceDecoration)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_set_grid_view -> {
                        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
                        popularMoviesRecyclerView.layoutManager = gridLayoutManager
                        true
                    }

                    R.id.action_set_list_view -> {
                        popularMoviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        true
                    }

                    R.id.action_search -> {
                        val searchView = topAppBar.menu.findItem(R.id.action_search).actionView as androidx.appcompat.widget.SearchView
                        searchView.setOnQueryTextListener(object :
                            androidx.appcompat.widget.SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(text: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                val filteredList = if (newText.isNullOrEmpty()) {
                                    favoritesViewModel.favoritesList
                                } else {
                                    val filterPattern = newText.toString().lowercase().trim()
                                    favoritesViewModel.favoritesList.filter {
                                        it.originalTitle?.lowercase()?.contains(filterPattern) == true
                                    }
                                }
                                moviesAdapter.list = filteredList.toMutableList()
                                return true
                            }
                        })
                        popularMoviesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        true
                    }

                    else -> false
                }
            }
        }
    }
}