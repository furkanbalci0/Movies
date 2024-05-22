package com.furkanbalci.movie.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.model.PopularMoviesModel

@Database(entities = [PopularMoviesModel.MovieModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun moviesDao(): MoviesDao
}