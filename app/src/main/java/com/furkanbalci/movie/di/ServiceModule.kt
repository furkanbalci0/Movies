package com.furkanbalci.movie.di

import android.content.Context
import androidx.room.Room
import com.furkanbalci.movie.data.local.database.AppDatabase
import com.furkanbalci.movie.data.remote.MoviesApiService
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import retrofit2.Retrofit

val serviceModule = module {

    single<MoviesApiService> { (baseUrl: String) ->
        val retrofit: Retrofit = get {
            parametersOf(baseUrl)
        }
        retrofit.create(MoviesApiService::class.java)
    }

    single<AppDatabase> {
        val context: Context = get()
        synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }

}