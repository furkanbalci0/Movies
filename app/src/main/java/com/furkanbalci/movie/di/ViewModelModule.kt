package com.furkanbalci.movie.di

import com.furkanbalci.movie.data.local.MoviesDao
import com.furkanbalci.movie.data.remote.repository.MoviesRepository
import com.furkanbalci.movie.ui.detail.DetailViewModel
import com.furkanbalci.movie.ui.favorites.FavoritesViewModel
import com.furkanbalci.movie.ui.home.HomeViewModel
import com.furkanbalci.movie.ui.viewmodel.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


val viewModelModule = module {

    viewModel<HomeViewModel> { (baseUrl: String) ->
        val repository: MoviesRepository = get {
            parametersOf(baseUrl)
        }
        val moviesDao: MoviesDao = get()
        HomeViewModel(repository, moviesDao)
    }

    viewModel<DetailViewModel> { (baseUrl: String) ->
        val repository: MoviesRepository = get {
            parametersOf(baseUrl)
        }
        val moviesDao: MoviesDao = get()
        DetailViewModel(repository, moviesDao)
    }

    viewModel<FavoritesViewModel> {
        val moviesDao: MoviesDao = get()
        FavoritesViewModel(moviesDao)
    }

    viewModel<SharedViewModel> {
        val moviesDao: MoviesDao = get()
        SharedViewModel(moviesDao)
    }
}