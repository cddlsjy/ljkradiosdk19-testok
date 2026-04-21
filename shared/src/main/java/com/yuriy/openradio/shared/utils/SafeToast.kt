package com.yuriy.openradio.shared.utils

import android.content.Context
import android.widget.Toast

object SafeToast {
    fun showAnyThread(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}
