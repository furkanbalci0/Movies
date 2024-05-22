package com.furkanbalci.movie.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.model.PopularMoviesModel
import com.furkanbalci.movie.data.remote.repository.MoviesRepository
import com.furkanbalci.movie.util.Constants
import com.furkanbalci.movie.util.Resource
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MoviesRepository,
    private val moviesDao: MoviesDao
) : ViewModel() {

    private val _popularMoviesLiveData: MutableLiveData<Resource<PopularMoviesModel>> = MutableLiveData()
    val popularMoviesLiveData: LiveData<Resource<PopularMoviesModel>> = _popularMoviesLiveData

    internal var currentPageNumber = 1
    internal var searchKey: String? = ""

    init {
        getPopularMovies(Constants.LANGUAGE, Constants.API_KEY, currentPageNumber)
    }

    fun getPopularMovies(
        language: String,
        apiKey: String,
        page: Int,
    ) {
        viewModelScope.launch {

            val favorites = moviesDao.getAll().map { it.id }
            repository.getPopularMovies(language, apiKey, page).buffer(10).collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { data ->
                            data.results?.filterNotNull()?.let { items ->
                                items.forEach { item ->
                                    if (item.id in favorites) {
                                        item.isFavorite = true
                                    }
                                }
                                _popularMoviesLiveData.postValue(Resource.Success(data))
                            }
                        }
                    }

                    else -> {
                        _popularMoviesLiveData.postValue(it)
                    }
                }
            }
        }
    }
}