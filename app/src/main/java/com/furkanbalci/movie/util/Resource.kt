package com.furkanbalci.movie.util

sealed class Resource<out T> {

    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error(val error: String?) : Resource<Nothing>()
    data class Loading(val loading: Boolean) : Resource<Nothing>()

}