/*
REFS:
https://howtodoandroid.com/continuous-location-updates-android/
https://medium.com/@msaudi/android-getting-user-location-updates-in-a-background-service-and-draw-location-updates-on-a-map-225589d28cf6
 */
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
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.app.App
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.receiver.MyBroadcastReceiver
import com.google.android.gms.location.*

class MyLocationService : Service() {

    private val TAG = "MyLocationService"
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private var LOCATION_REFRESH_TIME = 3000

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val currentLocation = locationResult.lastLocation
            Log.e(TAG, currentLocation.toString())

            //--- Broadcast location
            val intent = Intent(AppConfig.LOCATION_UPDATE)
            intent.putExtra("lat", currentLocation.latitude)
            intent.putExtra("lng", currentLocation.longitude)
            intent.putExtra("brn", currentLocation.bearing)
            intent.putExtra("alt", currentLocation.altitude)
            sendBroadcast(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        initService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotification()
        startLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false
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

//        val notificationIntent = Intent(this, HomeActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            1000,
//            notificationIntent, 0
//        )

        val actionIntent = Intent(this, MyBroadcastReceiver::class.java).apply {
            action = AppConfig.ACTION_STOP_SERVICE
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }

        val flag = if (Build.VERSION.SDK_INT >= 30) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
        val actionPendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, actionIntent, flag)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.info_location))
                .setSmallIcon(R.drawable.ic_stat_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(
                    R.drawable.ic_baseline_close_24,
                    getString(R.string.action_turn_off),
                    actionPendingIntent
                )

        startForeground(1, builder.build());
    }

    private fun initService() {
        locationRequest = LocationRequest.create()
        locationRequest!!.interval = LOCATION_REFRESH_TIME.toLong()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;

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