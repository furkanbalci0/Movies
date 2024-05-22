package com.furkanbalci.movie

import android.app.Application
import com.furkanbalci.movie.di.networkModule
import com.furkanbalci.movie.di.repositoryModule
import com.furkanbalci.movie.di.serviceModule
import com.furkanbalci.movie.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MoviesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MoviesApplication)
            androidLogger(Level.DEBUG)
            modules(
                networkModule, serviceModule, repositoryModule, viewModelModule
            )
        }
    }
}