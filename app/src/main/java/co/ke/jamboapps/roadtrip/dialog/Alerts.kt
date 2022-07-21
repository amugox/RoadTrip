package co.ke.jamboapps.roadtrip.dialog

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import co.ke.jamboapps.roadtrip.R

class Alerts {

    interface AlertActionListener {
        fun onPositiveAction()

        fun onNegativeAction(){}
    }

    companion object {

        fun showWarningMessage(context: Activity, messageRes: Int, listener: View.OnClickListener) {
            val message = context.getString(messageRes)
            showWarningMessage(context, message, listener)
        }

        fun showWarningMessage(context: Activity, messageRes: Int) {
            val message = context.getString(messageRes)
            showWarningMessage(context, message, null)
        }

        fun showWarningMessage(context: Activity, message: String) {
            showWarningMessage(context, message, null)
        }

        fun showWarningMessage(
            context: Activity,
            message: String,
            listener: View.OnClickListener?
        ) {

            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.alerts_error_view)
            dialog.setCancelable(false)

            val text = dialog.findViewById<TextView>(R.id.alert_text)
            text.text = message

            val dialogButton = dialog.findViewById<Button>(R.id.alert_button)
            dialogButton.setOnClickListener { v ->
                dialog.dismiss()
                listener?.onClick(v)
            }

            dialog.show()
        }

        fun showConfirmMessage(context: Activity, messageRes: Int, listener: AlertActionListener) {
            val message = context.getString(messageRes)
            showConfirmMessage(context, message, listener)
        }

        fun showConfirmMessage(context: Activity, message: String, listener: AlertActionListener?) {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.alerts_qstn_view)
            //dialog.setCancelable(false);

            val text = dialog.findViewById<TextView>(R.id.alert_text)
            text.text = message

            val yesButton = dialog.findViewById<Button>(R.id.yes_button)
            val noButton = dialog.findViewById<Button>(R.id.no_button)
            yesButton.setOnClickListener { v ->
                dialog.dismiss()
                listener?.onPositiveAction()
            }
            noButton.setOnClickListener { v ->
                dialog.dismiss()
                listener?.onNegativeAction()
            }

            dialog.show()

        }

        fun showSuccess(context: Activity, message: String) {
            showSuccess(context, message, null)
        }

        fun showSuccess(context: Activity, messageRes: Int) {
            val message = context.getString(messageRes)
            showSuccess(context, message, null)
        }

        fun showSuccess(context: Activity, messageRes: Int, listener: View.OnClickListener) {
            val message = context.getString(messageRes)
            showSuccess(context, message, listener)
        }

        fun showSuccess(context: Activity, message: String, listener: View.OnClickListener?) {
            var myMessage = message
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.alerts_success_view)
            if (listener != null)
                dialog.setCancelable(false)

            val text = dialog.findViewById<TextView>(R.id.alert_text)
            myMessage = myMessage.replace("\\\n", System.getProperty("line.separator")!!)
            text.text = myMessage

            val yesButton = dialog.findViewById<Button>(R.id.alert_button)
            yesButton.setOnClickListener { v ->
                dialog.dismiss()
                listener?.onClick(v)
            }

            dialog.show()
        }
    }
}