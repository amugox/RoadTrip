package co.ke.jamboapps.roadtrip.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import co.ke.jamboapps.roadtrip.R

class WaitDialog(context: Context) {

    var ctx: Context? = null
    var dialog:Dialog?=null

    init {
        ctx = context
    }

    fun show() {

        dialog = Dialog(ctx!!)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.progressdialog_view)
        dialog?.setCancelable(false)

        dialog?.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog?.show()
    }

    fun dismiss(){
        dialog?.dismiss()
    }
}