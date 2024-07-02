package eramo.resultgate.presentation.ui.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.TextView
import eramo.resultgate.R

class WarningDialog {

    companion object {
        private lateinit var dialog: AlertDialog

        fun init(activity: Activity) {
            dialog = AlertDialog.Builder(activity, R.style.DialogStyle)
                .setView(View.inflate(activity, R.layout.dialog_warning, null))
                .setCancelable(false)
                .create()
        }

        fun showDialog(title: String, message: String) {
            if (!dialog.isShowing) {
                dialog.show()
                val tvTitle = dialog.findViewById<TextView>(R.id.DFWarning_tv_title)
                val tvMessage = dialog.findViewById<TextView>(R.id.DFWarning_tv_message)
                val btnOk = dialog.findViewById<Button>(R.id.DFWarning_btn_ok)

                tvTitle.text = title
                tvMessage.text = message
                btnOk.setOnClickListener { dialog.dismiss() }
            }
        }

        fun dismissDialog() {
            if (dialog.isShowing) dialog.dismiss()
        }
    }
}