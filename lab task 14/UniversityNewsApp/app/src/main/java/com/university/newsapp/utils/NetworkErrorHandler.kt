package com.university.newsapp.utils

import retrofit2.HttpException
import java.io.IOException

object NetworkErrorHandler {

    fun getErrorMessage(throwable: Throwable): String {
        return when (throwable) {
            is HttpException -> "Server error: ${throwable.code()}"
            is IOException -> "Network error. Check your connection."
            else -> "Something went wrong: ${throwable.localizedMessage ?: throwable.message}"
        }
    }
}
