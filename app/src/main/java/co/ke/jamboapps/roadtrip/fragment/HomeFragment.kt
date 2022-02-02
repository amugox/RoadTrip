package co.ke.jamboapps.roadtrip.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.RoadMapActivity
import co.ke.jamboapps.roadtrip.service.MyLocationService

class HomeFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<Button>(R.id.btnStart).setOnClickListener {
            val ctx = requireActivity().applicationContext
            ContextCompat.startForegroundService(ctx, Intent(ctx, MyLocationService::class.java))
        }
        view.findViewById<Button>(R.id.btnStop).setOnClickListener {
            requireActivity().stopService(
                Intent(
                    requireActivity().applicationContext,
                    MyLocationService::class.java
                )
            )
        }
        view.findViewById<Button>(R.id.btnNav).setOnClickListener {
            val intent = Intent(requireActivity().applicationContext, RoadMapActivity::class.java)
            startActivity(intent)
        }

        if (!checkPermission()) {
            requestPermission()
        }


        return view
    }


    private fun checkPermission(): Boolean {
        val ctx = requireActivity().applicationContext
        val result =
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 =
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
    }
}