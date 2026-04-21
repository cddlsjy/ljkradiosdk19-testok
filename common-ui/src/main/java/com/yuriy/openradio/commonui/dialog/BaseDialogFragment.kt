package com.yuriy.openradio.commonui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }
}
