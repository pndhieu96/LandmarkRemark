package com.example.landmarkremark.base

/**
 * Class represent the result of a network request
 */
sealed class NetworkResult<out T: Any> {
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()
    data class Error(val exception: Exception) : NetworkResult<Nothing>()
}