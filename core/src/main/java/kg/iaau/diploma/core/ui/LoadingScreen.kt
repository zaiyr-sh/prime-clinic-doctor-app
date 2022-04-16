package kg.iaau.diploma.core.ui

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import kg.iaau.diploma.core.R

object LoadingScreen {

    var dialog: Dialog? = null

    // context(parent (reference))
    fun showLoading(context: Context?, cancelable: Boolean = false, text: String? = null) {
        if (dialog?.isShowing == true) return
        dialog = Dialog(context!!)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.layout_loading_screen)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCancelable(cancelable)
        val textView = dialog?.findViewById<TextView>(R.id.tv_title)
        text?.let {
            textView?.text = it
        }
        try {
            dialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoading() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}