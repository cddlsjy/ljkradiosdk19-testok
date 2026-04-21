package com.yuriy.openradio.shared.utils

import android.util.Log

/**
 * Logger utility for the OpenRadio application.
 * Provides consistent logging across the app.
 */
object AppLogger {

    private const val TAG = "OpenRadio"

    fun d(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message)
        }
    }

    fun i(message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message)
        }
    }

    fun w(message: String) {
        Log.w(TAG, message)
    }

    fun e(message: String) {
        Log.e(TAG, message)
    }

    fun e(message: String, throwable: Throwable?) {
        Log.e(TAG, message, throwable)
    }
}
