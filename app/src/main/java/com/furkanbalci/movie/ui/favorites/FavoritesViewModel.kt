package com.furkanbalci.movie.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.model.PopularMoviesModel
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val moviesDao: MoviesDao
) : ViewModel() {

    private val _favoritesMoviesLiveData: MutableLiveData<List<PopularMoviesModel.MovieModel>> = MutableLiveData()
    val favoritesMoviesLiveData: LiveData<List<PopularMoviesModel.MovieModel>> = _favoritesMoviesLiveData

    internal var favoritesList: Set<PopularMoviesModel.MovieModel> = mutableSetOf()

    fun getFavorites() {
        viewModelScope.launch {
            val list = moviesDao.getAll()
            favoritesList = list.toSet()
            _favoritesMoviesLiveData.postValue(list)
        }
    }

}