package com.furkanbalci.movie.data.remote

import com.furkanbalci.movie.data.model.MovieDetailModel
import com.furkanbalci.movie.data.model.PopularMoviesModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApiService {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String,
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
    ): Response<PopularMoviesModel>


    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String,
        @Query("api_key") apiKey: String
    ): Response<MovieDetailModel>

}