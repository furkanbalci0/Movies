package com.furkanbalci.movie.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.furkanbalci.movie.data.model.PopularMoviesModel

@Dao
interface MoviesDao {

    @Query("SELECT * FROM MovieModel")
    suspend fun getAll(): List<PopularMoviesModel.MovieModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(model: PopularMoviesModel.MovieModel)

    @Delete
    suspend fun delete(model: PopularMoviesModel.MovieModel)
}