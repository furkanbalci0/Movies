package com.furkanbalci.movie.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.model.PopularMoviesModel
import kotlinx.coroutines.launch

class SharedViewModel(
    private val moviesDao: MoviesDao
) : ViewModel() {

    private var _updateMovieLiveData: MutableLiveData<PopularMoviesModel.MovieModel> = MutableLiveData()
    val updateMovieLiveData: LiveData<PopularMoviesModel.MovieModel> = _updateMovieLiveData

    internal var moviesList: MutableSet<PopularMoviesModel.MovieModel> = mutableSetOf()

    fun setFavoriteItem(movieModel: PopularMoviesModel.MovieModel) {
        viewModelScope.launch {
            movieModel.isFavorite = !movieModel.isFavorite
            if (movieModel.isFavorite) {
                moviesDao.insert(movieModel)
            } else {
                moviesDao.delete(movieModel)
            }
            _updateMovieLiveData.postValue(movieModel)
        }
    }

}