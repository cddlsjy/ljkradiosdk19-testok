package com.yuriy.openradio.commonui

import android.view.View

object UiUtils {
    fun visible(view: View) {
        view.visibility = View.VISIBLE
    }

    fun gone(view: View) {
        view.visibility = View.GONE
    }

    fun invisible(view: View) {
        view.visibility = View.INVISIBLE
    }
}
