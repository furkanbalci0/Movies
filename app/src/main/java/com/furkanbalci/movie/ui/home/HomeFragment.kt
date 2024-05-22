package com.furkanbalci.movie.ui.home

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
import com.furkanbalci.movie.databinding.FragmentHomeBinding
import com.furkanbalci.movie.ui.adapter.MoviesAdapter
import com.furkanbalci.movie.ui.adapter.SpaceItemDecoration
import com.furkanbalci.movie.ui.viewmodel.SharedViewModel
import com.furkanbalci.movie.util.Constants
import com.furkanbalci.movie.util.Resource
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private val sharedViewModel: SharedViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel {
        parametersOf(Constants.BASE_URL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observePopularMovies()
        observeFavorites()
    }

    private fun observePopularMovies() {

        lifecycleScope.launch {
            homeViewModel.popularMoviesLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Error -> {
                        Snackbar.make(binding.root, "$it", Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> {
                        if (it.loading) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }
                    }

                    is Resource.Success -> {
                        it.data?.let { data ->
                            data.results?.filterNotNull()?.let { items ->
                                moviesAdapter.list = (moviesAdapter.list + items).toMutableList()
                                sharedViewModel.moviesList += items.toMutableSet()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun observeFavorites() {
        lifecycleScope.launch {
            sharedViewModel.updateMovieLiveData.observe(viewLifecycleOwner) { movieModel ->
                moviesAdapter.updateItem(movieModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        moviesAdapter.list = sharedViewModel.moviesList.toMutableList()
    }

    private fun initRecyclerView() {
        moviesAdapter = MoviesAdapter(
            onItemClick = { movieModel ->
                findNavController().navigate(R.id.action_homeFragment_to_detailFragment, Bundle().apply {
                    putInt(Constants.MOVIE_ID_KEY, movieModel.id)
                })
            },
            onStarClick = { movieModel ->
                sharedViewModel.setFavoriteItem(movieModel)
            },
            onLoadMoreClick = {
                homeViewModel.getPopularMovies(Constants.LANGUAGE, Constants.API_KEY, ++homeViewModel.currentPageNumber)
            })
        moviesAdapter.list = sharedViewModel.moviesList.toMutableList()

        binding.apply {
            popularMoviesRecyclerView.adapter = moviesAdapter

            val spaceDecoration = SpaceItemDecoration(16)
            popularMoviesRecyclerView.addItemDecoration(spaceDecoration)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_set_grid_view -> {
                        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
                        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return if (moviesAdapter.getItemViewType(position) != moviesAdapter.MOVIE_VIEW_TYPE) gridLayoutManager.spanCount else 1
                            }
                        }
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
                                homeViewModel.searchKey = newText
                                val filteredList = if (newText.isNullOrEmpty()) {
                                    sharedViewModel.moviesList
                                } else {
                                    val filterPattern = newText.toString().lowercase().trim()
                                    sharedViewModel.moviesList.filter {
                                        it.originalTitle?.lowercase()?.contains(filterPattern) == true
                                    }
                                }
                                moviesAdapter.list = filteredList.toMutableList()
                                return true
                            }
                        })
                        true
                    }

                    else -> false
                }
            }
        }
    }
}