package co.ke.jamboapps.roadtrip.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.db.RouteMark
import co.ke.jamboapps.roadtrip.service.MyLocationService
import co.ke.jamboapps.roadtrip.service.RouteMarkService
import co.ke.jamboapps.roadtrip.util.RouteUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MyBroadcastReceiver : BroadcastReceiver() {

    private var fusedLocationClient: FusedLocationProviderClient? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.e("MyBroadcastReceiver", intent.action ?: "empty")
        val callerId = intent.getIntExtra("caller_id", 0)
        if (callerId == 0) {
            if (intent.action == AppConfig.ACTION_STOP_SERVICE) {
                context.stopService(Intent(context, MyLocationService::class.java))
            }
        } else if (callerId == 1) {
            val routeCode = intent.getIntExtra("rt_code", 0)
            if (intent.action == AppConfig.ACTION_END_ROUTE_MARKING) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient?.let {
                    it.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            RouteUtil.createMark(routeCode, 3, location)
                        }

                        //--- Stop background service
                        endRouteMarking(context, routeCode)
                    }
                }

            } else if (intent.action == AppConfig.ACTION_CANCEL_ROUTE_MARKING) {
                cancelRouteMarking(context, routeCode)
            }
        }
    }

    private fun stopMarkerService(context: Context) {
        val intentStop = Intent(context, RouteMarkService::class.java)
        intentStop.action = AppConfig.ACTION_STOP_SERVICE
        context.stopService(intentStop)

        context.sendBroadcast(Intent(AppConfig.ACTION_SERVICE_STOPPED))
    }

    private fun endRouteMarking(context: Context, code: Int) {
        //--- Update status
        val route = MyRoute.get(code)
        route?.let {
            it.markStat = 2
            it.update()
        }

        //--- Post data
        RouteUtil.uploadMarks(context, code)

        stopMarkerService(context)
    }

    private fun cancelRouteMarking(context: Context, code: Int) {
        //--- Clear marked data
        RouteMark.clearMarks(code)
        stopMarkerService(context)
    }
}