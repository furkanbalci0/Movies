package com.furkanbalci.movie.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

object NetworkHandler {

    fun <T> runRepositorySafe(block: suspend () -> Response<T>): Flow<Resource<T>> = flow {
        emit(Resource.Loading(true))
        try {
            val responseBody = block()
            if (responseBody.isSuccessful && responseBody.body() != null) {
                val data = responseBody.body()
                emit(Resource.Success(data))
            } else {
                val httpError = responseBody.errorBody()?.string().toString()
                emit(Resource.Error(httpError))
            }
        } catch (exception: Exception) {
            emit(Resource.Error(exception.localizedMessage))
        }
        delay(2)
        emit(Resource.Loading(false))
    }

}
