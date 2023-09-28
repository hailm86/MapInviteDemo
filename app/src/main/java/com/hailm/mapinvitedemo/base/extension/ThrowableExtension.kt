package com.hailm.mapinvitedemo.base.extension

import com.hailm.mapinvitedemo.base.model.AppErrorDialog
import com.hailm.mapinvitedemo.cloud.response.exception.AppException

fun Throwable.toErrorDialog(): AppErrorDialog {
    return when (this) {
        is AppException -> {
            when (this) {
                is AppException.BlockedUserException -> {
                    AppErrorDialog(
                        title = "",
                        message = "We are sorry you cannot log in to this App.\nPlease contact your Administrator for access"
                    )
                }

                is AppException.EmailNotFoundException -> {
                    AppErrorDialog(
                        title = "Invalid Email Address ",
                        message = "Invalid email address. Please, check your entry and try again"
                    )
                }

                else -> {
                    with(errorResponse) {
                        AppErrorDialog(title = error, message = message)
                    }
                }
            }
        }

        else -> {
            AppErrorDialog(message = message.toString())
        }
    }
}
