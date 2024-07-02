package eramo.tahoon.presentation.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.MutableLiveData
import eramo.tahoon.R

class LoadingDialog {

    companion object {
        private lateinit var dialog: AlertDialog
        val cancelCurrentRequest = MutableLiveData<Boolean>()

        fun init(activity: Activity) {
            val view = View.inflate(activity, R.layout.dialog_loading, null)
//            val tvLoading = view.findViewById<TextView>(R.id.dialogLoading_tv_loading)
//            tvLoading.text = if (LocalHelperUtil.isEnglish()) "Loading.." else "جار التحميل.."

            dialog = AlertDialog.Builder(activity, R.style.DialogStyle)
                .setView(view)
                .setCancelable(false)
                .setOnKeyListener { _, keyCode, _ ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        cancelCurrentRequest.value = true
                    }
                    true
                }.create()
        }

        fun showDialog() {
            if (!dialog.isShowing) dialog.show()
        }

        fun dismissDialog() {
            if (dialog.isShowing) dialog.dismiss()
        }
    }
}