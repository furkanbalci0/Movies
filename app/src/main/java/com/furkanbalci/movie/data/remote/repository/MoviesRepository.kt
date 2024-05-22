package com.furkanbalci.movie.data.remote.repository

import com.furkanbalci.movie.data.model.MovieDetailModel
import com.furkanbalci.movie.data.model.PopularMoviesModel
import com.furkanbalci.movie.util.Resource
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    fun getPopularMovies(
        language: String,
        apiKey: String,
        page: Int,
    ): Flow<Resource<PopularMoviesModel>>

    fun getMovieDetail(
        movieId: Int,
        language: String,
        apiKey: String
    ): Flow<Resource<MovieDetailModel>>
}