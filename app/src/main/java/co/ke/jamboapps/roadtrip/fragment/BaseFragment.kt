package co.ke.jamboapps.roadtrip.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import co.ke.jamboapps.roadtrip.util.AppUtil

open class BaseFragment : Fragment() {

    interface OnActionListener {
        fun onAction(action: Int, data: Bundle)
    }

    var baseListener: OnActionListener? = null
    var myContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnActionListener) {
            baseListener = context
        }
        myContext = context
    }

    override fun onDetach() {
        super.onDetach()
        baseListener = null
    }

    fun showErrorMessage(message: String) {
        myContext?.let { AppUtil.showToast(it, message) }
    }
}