package co.ke.jamboapps.roadtrip.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.app.App
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.clazz.HttpReqActions

class AppUtil {
    companion object {
        fun getAppVersion(): String {
            val ctx = App.getAppInstance().applicationContext
            var pInfo: PackageInfo? = null
            try {
                pInfo = ctx?.packageManager?.getPackageInfo(ctx.packageName, 0)
                return pInfo!!.versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

            return "0.0.0"
        }

        fun isInternetAvailable(ctx: Context): Boolean {
            var isConnected = false
            try {
                val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val activeNetwork = cm.activeNetworkInfo
                isConnected = activeNetwork != null && activeNetwork.isConnected

                if (!isConnected) {
                    val networkStateIntent = Intent(AppConfig.ACTION_NETWORK_STATE_CHANGE)
                    networkStateIntent.putExtra(AppConfig.IS_NETWORK_AVAILABLE, false)
                    LocalBroadcastManager.getInstance(ctx).sendBroadcast(networkStateIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return isConnected
        }

        fun getServerUrl(actionType: Int): String {
            val ctx: Context = App.getAppInstance().applicationContext
            val baseUrl = ctx.getString(R.string.server_base_url)
            val endpoint = when (actionType) {
                HttpReqActions.ACCOUNT_CREATE,
                HttpReqActions.ACCOUNT_REGISTER ->
                    ctx.getString(R.string.account_operation_ep)
                HttpReqActions.ROUTE_CREATE ->
                    ctx.getString(R.string.route_operation_ep)
                else -> ""
            }
            return String.format("%s/%s", baseUrl, endpoint)
        }

        fun showToast(context: Context, message: String) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)

            val group = toast.view as ViewGroup
            val tv = group.getChildAt(0) as TextView
            tv.textSize = 13f

            val typeface = ResourcesCompat.getFont(context, R.font.app_font)
            tv.typeface = typeface

            toast.show()
        }

        fun showToast(context: Context, messageRes: Int) {
            val message = context.getString(messageRes)
            showToast(context, message)
        }
    }
}