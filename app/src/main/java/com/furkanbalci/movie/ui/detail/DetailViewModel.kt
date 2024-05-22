package com.furkanbalci.movie.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.model.MovieDetailModel
import com.furkanbalci.movie.data.model.PopularMoviesModel
import com.furkanbalci.movie.data.remote.repository.MoviesRepository
import com.furkanbalci.movie.util.Resource
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MoviesRepository,
    private val moviesDao: MoviesDao
) : ViewModel() {

    private val _movieDetailLiveData: MutableLiveData<Resource<MovieDetailModel>> = MutableLiveData()
    val movieDetailLiveData: LiveData<Resource<MovieDetailModel>> = _movieDetailLiveData

    private var _favoriteMovieLiveData: MutableLiveData<MovieDetailModel> = MutableLiveData()
    val favoriteMovieLiveData: LiveData<MovieDetailModel> = _favoriteMovieLiveData

    fun getDetail(
        movieId: Int,
        language: String,
        apiKey: String,
    ) {
        viewModelScope.launch {

            val favorites = moviesDao.getAll()
            repository.getMovieDetail(movieId, language, apiKey).buffer(10).collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let { data ->
                            if (data.id in favorites.map { movie -> movie.id }) {
                                data.isFavorite = true
                            }

                            _movieDetailLiveData.postValue(Resource.Success(data))
                        }
                    }

                    else -> {
                        _movieDetailLiveData.postValue(it)
                    }
                }
            }
        }
    }

    fun setFavoriteItem(movieModel: MovieDetailModel) {
        viewModelScope.launch {
            movieModel.isFavorite = !movieModel.isFavorite
            val item = PopularMoviesModel.MovieModel(
                adult = movieModel.adult,
                id = movieModel.id!!,
                title = movieModel.title,
                isFavorite = movieModel.isFavorite,
                originalLanguage = movieModel.originalLanguage,
                video = movieModel.video,
                overview = movieModel.overview,
                voteCount = movieModel.voteCount,
                backdropPath = movieModel.backdropPath,
                originalTitle = movieModel.originalTitle,
                popularity = movieModel.popularity,
                posterPath = movieModel.posterPath,
                releaseDate = movieModel.releaseDate,
                voteAverage = movieModel.voteAverage
            )
            if (movieModel.isFavorite) {
                moviesDao.insert(item)
            } else {
                moviesDao.delete(item)
            }
            _favoriteMovieLiveData.postValue(movieModel)
        }
    }
}