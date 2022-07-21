package co.ke.jamboapps.roadtrip.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.app.App
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.db.RouteMark
import co.ke.jamboapps.roadtrip.receiver.MyBroadcastReceiver
import com.google.android.gms.location.*

class RouteMarkService : Service() {

    companion object {
        private const val TAG = "RouteMarkService"
        private const val NOTIFICATION_CHANNEL_ID = "my_route_mark_service"
        private const val LOCATION_REFRESH_TIME = 5000
        private const val METERS_BTN = 100.0
    }

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var mLocation: Location? = null
    private var routeCode = 0

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            Log.e(TAG, currentLocation.toString())

            currentLocation?.let {
                var saveToDb = true
                if (mLocation != null) {
                    val dist = currentLocation.distanceTo(mLocation)
                    Log.e(TAG, "Meters between: $dist, Route: $routeCode")
                    if (dist < METERS_BTN)
                        saveToDb = true
                    else
                        mLocation = it
                } else {
                    mLocation = it
                }

                //--- Save
                if (saveToDb) {
                    val mark = RouteMark(code = routeCode, lat = it.latitude, lon = it.longitude)
                    mark.save()
                }

                //--- Broadcast location
                val intent = Intent(AppConfig.LOCATION_UPDATE)
                intent.putExtra("lat", it.latitude)
                intent.putExtra("lng", it.longitude)
                intent.putExtra("brn", it.bearing)
                intent.putExtra("alt", it.altitude)
                sendBroadcast(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(AppConfig.ACTION_STOP_SERVICE)) {
            stopSelf()
        } else {
            routeCode = intent!!.getIntExtra("rt_code", 0)
            createNotification()
            startLocationUpdates()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mFusedLocationClient!!.removeLocationUpdates(locationCallback);
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = NOTIFICATION_CHANNEL_ID
            channel.setSound(null, null)

            val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        val endIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = AppConfig.ACTION_END_ROUTE_MARKING
            putExtra(NotificationCompat.EXTRA_NOTIFICATION_ID, 0)
            putExtra("caller_id", 1)
            putExtra("rt_code", routeCode)
        }

        val cancelIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = AppConfig.ACTION_CANCEL_ROUTE_MARKING
            putExtra(NotificationCompat.EXTRA_NOTIFICATION_ID, 0)
            putExtra("caller_id", 1)
            putExtra("rt_code", routeCode)
        }

        val flag = if (Build.VERSION.SDK_INT >= 30) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        val endPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, endIntent, flag)
        val cancelPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, cancelIntent, flag)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.info_auto_mark_service))
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(
                    R.drawable.ic_baseline_close_24,
                    getString(R.string.action_end),
                    endPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_close_24,
                    getString(R.string.action_cancel),
                    cancelPendingIntent
                )

        startForeground(1, builder.build());
    }

    private fun initService() {
        locationRequest = LocationRequest.create()
        locationRequest!!.interval = LOCATION_REFRESH_TIME.toLong()
        locationRequest!!.priority = Priority.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient =
            LocationServices.getFusedLocationProviderClient(App.getAppInstance())
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            Looper.myLooper()!!
        )
    }
}