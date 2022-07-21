package co.ke.jamboapps.roadtrip

import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import co.ke.jamboapps.roadtrip.databinding.ActivityReviewRouteBinding
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.db.RouteMark
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class ReviewRouteActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityReviewRouteBinding
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mSettingsClient: SettingsClient? = null
    private var mCurrentLocation: LatLng? = null
    private var mCurrLocMarker: Marker? = null
    private var myLocIcon: Bitmap? = null
    private var routeCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReviewRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupHomeButton()

        routeCode = intent.getIntExtra("rt_code", 0)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)

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

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mFusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
            run {
                mCurrentLocation = LatLng(loc!!.latitude, loc.longitude)
                updateMyLocation()
            }
        }

        val points = mutableListOf<LatLng>()
        val marks = RouteMark.getByRoute(routeCode)
        marks.forEach { m ->
            points.add(LatLng(m.lat, m.lon))
        }
        mMap.addPolyline(
            PolylineOptions()
                .color(ContextCompat.getColor(this, R.color.colorError))
                .width(4.0F)
                .addAll(points)
        )
    }

    private fun updateMyLocation() {

        if (mCurrentLocation != null) {
            if (mCurrLocMarker == null) {
                mCurrLocMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(mCurrentLocation!!)
                        .icon(BitmapDescriptorFactory.fromBitmap(myLocIcon!!))
                        .title("My Location")
                )
            } else {
                mCurrLocMarker!!.position = mCurrentLocation!!
            }

            val cameraPosition = CameraPosition.Builder()
                .target(mCurrentLocation!!) // Sets the center of the map to Mountain View
                .zoom(16f) // Sets the zoom
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
}