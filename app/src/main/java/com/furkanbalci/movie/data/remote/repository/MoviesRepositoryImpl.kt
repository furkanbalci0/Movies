package com.furkanbalci.movie.data.remote.repository

import com.furkanbalci.movie.data.model.MovieDetailModel
import com.furkanbalci.movie.data.model.PopularMoviesModel
import com.furkanbalci.movie.data.remote.MoviesApiService
import com.furkanbalci.movie.util.NetworkHandler.runRepositorySafe
import com.furkanbalci.movie.util.Resource
import kotlinx.coroutines.flow.Flow

class MoviesRepositoryImpl(
    private val moviesApiService: MoviesApiService
) : MoviesRepository {

    override fun getPopularMovies(
        language: String,
        apiKey: String,
        page: Int
    ): Flow<Resource<PopularMoviesModel>> = runRepositorySafe {
        moviesApiService.getPopularMovies(language, apiKey, page)
    }

    override fun getMovieDetail(
        movieId: Int,
        language: String,
        apiKey: String
    ): Flow<Resource<MovieDetailModel>> = runRepositorySafe {
        moviesApiService.getMovieDetail(movieId, language, apiKey)
    }

}