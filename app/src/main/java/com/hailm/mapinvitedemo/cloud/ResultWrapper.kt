package com.hailm.mapinvitedemo.cloud

import com.hailm.mapinvitedemo.base.model.response.ErrorResponse
import com.hailm.mapinvitedemo.cloud.response.exception.AppException
import java.io.IOException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class GenericError(
        val code: Int? = null,
        val error: ErrorResponse? = null,
        val throwable: Throwable? = null
    ) : ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()

    @Throws(Exception::class)
    fun takeValueOrThrow(): T {
        when (this) {
            is Success -> {
                return value
            }

            is GenericError -> {
                when (error) {
                    null -> throw throwable ?: Throwable()
                    else -> throw AppException.mapToException(error)
                }
            }

            is NetworkError -> throw IOException()
        }
    }
}

