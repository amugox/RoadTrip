package co.ke.jamboapps.roadtrip

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import co.ke.jamboapps.roadtrip.app.AppConfig
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class RoadMapActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var mMap: GoogleMap
    private lateinit var receiver: BroadcastReceiver

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mSettingsClient: SettingsClient? = null
    private var mCurrentLocation: LatLng? = null
    private var mCurrLocMarker: Marker? = null
    private var myLocIcon: Bitmap? = null

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_road_map)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //--- Create Icon
        val drawable = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.ic_baseline_my_location_24
        )
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable!!),
            ContextCompat.getColor(applicationContext, R.color.colorSuccess)
        )
        myLocIcon = drawable.toBitmap()

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

        mFusedLocationClient!!.lastLocation.addOnSuccessListener { loc: Location? ->
            run {
                mCurrentLocation = LatLng(loc!!.latitude, loc.longitude)
            }
        }

        //----- Initialize map
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                mMap?.let {
                    val lat = intent!!.getDoubleExtra("lat", 0.0)
                    val lon = intent!!.getDoubleExtra("lng", 0.0)
                    mCurrentLocation = LatLng(lat, lon)

                    updateMyLocation()

//                    val cameraPos = CameraPosition.Builder()
//                        .target(mCurrentLocation!!) // Sets the center of the map to Mountain View
//                        .zoom(15f) // Sets the zoom
//                        .tilt(45f) // Sets the tilt of the camera to 30 degrees
//                        .build()

                    //val icon = BitmapFactory.decodeResource(R.drawable.ic_baseline_my_location_24)
//                    val myIcon = AppCompatResources.getDrawable(
//                        applicationContext,
//                        R.drawable.ic_baseline_my_location_24
//                    )
//                    val bitmap = ContextCompat.getDrawable(
//                        applicationContext,
//                        R.drawable.ic_baseline_my_location_24
//                    )?.toBitmap()
//                    it.addMarker(
//                        MarkerOptions().position(mCurrentLocation!!).title("My Location").icon(
//                            BitmapDescriptorFactory.fromBitmap(bitmap!!)
//                        )
//                    )
//                    it.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos))
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //--- Add current location
        mCurrentLocation?.let {
//            val myLoc = LatLng(it.latitude, it.longitude)
////            mMap.addMarker(MarkerOptions().position(myLoc).title("Marker in Sydney"))
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLoc))

            updateMyLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        receiver?.let {
            val intentFilter = IntentFilter(AppConfig.LOCATION_UPDATE)
            registerReceiver(it, intentFilter)
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        receiver?.let { unregisterReceiver(it) }
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {

    }

    private fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
            false
        } else {
            true
        }
    }

    private fun updateMyLocation() {
        if (mMap == null)
            return

        if (mCurrentLocation != null) {
            if (mCurrLocMarker == null) {
                mCurrLocMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(mCurrentLocation!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(myLocIcon!!))
                        .title("My Location")
                        .flat(true)
                )
            } else {
                mCurrLocMarker!!.position = mCurrentLocation!!
            }

            val cameraPosition = CameraPosition.Builder()
                .target(mCurrentLocation!!) // Sets the center of the map to Mountain View
                .zoom(16f) // Sets the zoom
                .tilt(45f) // Sets the tilt of the camera to 30 degrees
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
}