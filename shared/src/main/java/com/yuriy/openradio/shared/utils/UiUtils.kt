package com.yuriy.openradio.shared.utils

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun <T : View> findView(view: View, id: Int): T {
    return view.findViewById(id)
}

fun findToolbar(view: View, id: Int): Toolbar {
    return view.findViewById(id)
}

fun findTextView(view: View, id: Int): TextView {
    return view.findViewById(id)
}

fun findProgressBar(view: View, id: Int): ProgressBar {
    return view.findViewById(id)
}

fun findImageView(view: View, id: Int): ImageView {
    return view.findViewById(id)
}

fun findCheckBox(view: View, id: Int): CheckBox {
    return view.findViewById(id)
}

fun findFloatingActionButton(view: View, id: Int): View {
    return view.findViewById(id)
}
