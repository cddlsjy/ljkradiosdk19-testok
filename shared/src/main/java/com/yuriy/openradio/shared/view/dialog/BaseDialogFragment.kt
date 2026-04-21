package com.yuriy.openradio.shared.view.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

open class BaseDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    companion object {
        const val DIALOG_TAG = "dialog"
        fun newInstance(dialogClassName: String): BaseDialogFragment {
            return BaseDialogFragment()
        }
    }
}
