package co.ke.jamboapps.roadtrip.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.model.MenuItem
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.utils.sizeDp

class AddRouteMarkDialog {
    interface ActionListener {
        fun onConfirm()

        fun onCancel() {}
    }

    companion object {
        fun show(context: Activity, item: MenuItem, listener: ActionListener?) {

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.add_route_mark_dialog)
            dialog.setCancelable(false)

            dialog.findViewById<TextView>(R.id.tvTitle).text = item.title

            dialog.findViewById<Button>(R.id.btnNo).setOnClickListener {
                dialog.dismiss()
                listener?.onCancel()
            }

            dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
                dialog.dismiss()
                listener?.onConfirm()
            }

            with(dialog.findViewById<ImageView>(R.id.ivImage)) {
                if (item.iconRes != 0) {
                    setImageResource(item.iconRes)
                }
                if (item.icon != null) {
                    val myIcon = IconicsDrawable(context, item.icon!!).apply {
                        sizeDp = 60
                    }
                    setImageDrawable(myIcon)
                }
            }

            dialog.show()
        }
    }
}