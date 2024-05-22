package com.furkanbalci.movie.di

import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.local.database.AppDatabase
import com.furkanbalci.movie.data.remote.MoviesApiService
import com.furkanbalci.movie.data.remote.repository.MoviesRepository
import com.furkanbalci.movie.data.remote.repository.MoviesRepositoryImpl
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val repositoryModule = module {

    single<MoviesRepository> { (baseUrl: String) ->
        val apiService: MoviesApiService = get {
            parametersOf(baseUrl)
        }
        MoviesRepositoryImpl(apiService)
    }

    single<MoviesDao> {
        val appDatabase: AppDatabase = get()
        appDatabase.moviesDao()
    }
}