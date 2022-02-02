package co.ke.jamboapps.roadtrip.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.service.MyLocationService

class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AppConfig.ACTION_STOP_SERVICE) {
            context.stopService(Intent(context, MyLocationService::class.java))
        }
    }
}