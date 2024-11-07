package com.vsca.vsnapvoicecollege.Utils

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Message
import android.view.WindowManager
import com.vsca.vsnapvoicecollege.R

object CustomLoading {
    fun createProgressDialog(context: Context?): ProgressDialog {
        val dialog = ProgressDialog(context)
        try {
            dialog.show()
        } catch (e: WindowManager.BadTokenException) {

        }
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.custom_loading)
        return dialog
    }
}