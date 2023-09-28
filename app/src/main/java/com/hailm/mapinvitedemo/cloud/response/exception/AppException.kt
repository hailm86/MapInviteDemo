package com.hailm.mapinvitedemo.cloud.response.exception

import com.hailm.mapinvitedemo.base.model.response.ErrorResponse

sealed class AppException(
    open val errorResponse: ErrorResponse = ErrorResponse(),
    open val throwable: Throwable? = null
) :
    Exception(
        errorResponse.message.takeIf { it.isNotEmpty() } ?: throwable?.message,
        throwable
    ) {
    class NotLoginException : AppException(
        errorResponse = ErrorResponse(message = "User not login")
    )

    data class BlockedUserException(override val errorResponse: ErrorResponse) :
        AppException(errorResponse)

    data class EmailNotFoundException(override val errorResponse: ErrorResponse) :
        AppException(errorResponse)

    data class NoKeyException(override val errorResponse: ErrorResponse) :
        AppException(errorResponse)

    data class UnknownException(override val throwable: Throwable? = null) :
        AppException(throwable = throwable)

    companion object {
        fun mapToException(errorResponse: ErrorResponse?): AppException {
            return when (errorResponse?.key) {
                null -> UnknownException()
                "accountBlocked" -> BlockedUserException(errorResponse)
                else -> mapToExceptionWitMessage(errorResponse)
            }
        }

        private fun mapToExceptionWitMessage(errorResponse: ErrorResponse): AppException {
            return when (errorResponse.message) {
                "Email not found" -> EmailNotFoundException(errorResponse)
                else -> NoKeyException(errorResponse)
            }
        }
    }
}
