package com.example.nasa.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Closed hierarchy to wrap the api response to handle
 * results gracefully
 */
sealed class Response<T> {
    data class Success<T>(val data: T): Response<T>()

    data class Error<T>(val error: String): Response<T>()
}

/**
 * converting the retrofit Response to a flow of Response
 */
fun <T> retrofit2.Response<T>.mapToResponse() = flow<Response<T>> {
    if(isSuccessful) {
        this@mapToResponse.body()?.let {
            emit(Response.Success(it))
        } ?: emit(Response.Error("Something went wrong! error: ${message()}"))
    } else {
        emit(Response.Error("error: ${message()}"))
    }
}.flowOn(Dispatchers.IO)