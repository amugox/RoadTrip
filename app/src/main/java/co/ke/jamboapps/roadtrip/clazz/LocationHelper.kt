package co.ke.jamboapps.roadtrip.clazz

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class LocationHelper {
    // 3 seconds. The Minimum Time to get location update
    var LOCATION_REFRESH_TIME = 3000

    // 0 meters. The Minimum Distance to be changed to get location update
    var LOCATION_REFRESH_DISTANCE = 0

    @SuppressLint("MissingPermission")
    fun startListeningUserLocation(context: Context, myListener: MyLocationListener) {
//        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val locationListener: LocationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                myListener.onLocationChanged(location) // calling listener to inform that updated location is available
//            }
//
//            override fun onProviderEnabled(provider: String) {}
//            override fun onProviderDisabled(provider: String) {}
//            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
//        }
//        mLocationManager.requestLocationUpdates(
//            LocationManager.GPS_PROVIDER,
//            LOCATION_REFRESH_TIME.toLong(),
//            LOCATION_REFRESH_DISTANCE.toFloat(),
//            locationListener
//        )

        //============================================
        val locationRequest = LocationRequest.create()
        locationRequest.interval = LOCATION_REFRESH_TIME.toLong();
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        mFusedLocationClient.requestLocationUpdates(locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val currentLocation = locationResult.lastLocation
                    myListener.onLocationChanged(currentLocation)
                }
            }, Looper.myLooper()!!
        )
        //===========================================
    }
}

interface MyLocationListener {
    fun onLocationChanged(location: Location?)
}