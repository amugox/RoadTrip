package co.ke.jamboapps.roadtrip.fragment

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.app.AppConfig
import co.ke.jamboapps.roadtrip.databinding.FragmentMarkRouteAutoBinding
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.db.RouteMark
import co.ke.jamboapps.roadtrip.service.RouteMarkService
import co.ke.jamboapps.roadtrip.util.RouteUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MarkRouteAutoFragment : BaseFragment() {

    private var _binding: FragmentMarkRouteAutoBinding? = null
    private val binding get() = _binding!!
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var receiver: BroadcastReceiver? = null
    private var locationPermReq =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startMarking()
            } else {
                Toast.makeText(
                    requireActivity(),
                    R.string.err_permission_denied,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkRouteAutoBinding.inflate(inflater, container, false)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent!!.action.equals(AppConfig.ACTION_SERVICE_STOPPED)) {
                    showHideViews(1)
                    Toast.makeText(
                        requireActivity(),
                        R.string.info_service_stopped,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnStart.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermReq.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                startMarking()
            }
        }

        binding.btnEnd.setOnClickListener { endRouteMarking() }
        binding.btnCancel.setOnClickListener { cancelRouteMarking() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (receiver != null) {
            requireActivity().registerReceiver(
                receiver,
                IntentFilter(AppConfig.ACTION_SERVICE_STOPPED)
            )
        }
    }

    override fun onPause() {
        if (receiver != null)
            requireActivity().unregisterReceiver(receiver)
        super.onPause()
    }

    private fun startMarking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val routeCode = requireArguments().getInt("rt_code", 0)

        //--- Clear previous marking
        RouteMark.clearMarks(routeCode)

        //--- Update status
        val route = MyRoute.get(routeCode)
        route?.let {
            it.markStat = 1
            it.update()
        }

        //-- Current location
        fusedLocationClient?.let {
            it.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    RouteUtil.createMark(routeCode, 1, location)
                }

                //--- Start background service
                val ctx = requireActivity().applicationContext
                val intent = Intent(ctx, RouteMarkService::class.java).apply {
                    putExtra("rt_code", routeCode)
                }
                ContextCompat.startForegroundService(ctx, intent)
            }
        }

        showHideViews(0)
    }

    private fun endRouteMarking() {
        val routeCode = requireArguments().getInt("rt_code", 0)

        //--- Get end point
        fusedLocationClient?.let {
            it.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    RouteUtil.createMark(routeCode, 3, location)
                }

                //--- Update status
                val route = MyRoute.get(routeCode)
                if (route != null) {
                    route.markStat = 2
                    route.update()
                }

                //--- Post data
                RouteUtil.uploadMarks(requireContext(), routeCode)

                stopMarkerService()
            }
        }
    }

    private fun cancelRouteMarking() {
        //--- Clear marked data
        val routeCode = requireArguments().getInt("rt_code", 0)
        RouteMark.clearMarks(routeCode)
        stopMarkerService()
    }

    private fun stopMarkerService() {
        val intentStop = Intent(context, RouteMarkService::class.java)
        intentStop.action = AppConfig.ACTION_STOP_SERVICE
        requireActivity().stopService(intentStop)

        showHideViews(1)
    }

    private fun showHideViews(flag: Int) {
        if (flag == 0) {
            binding.btnStart.visibility = View.GONE
            binding.viewActions.visibility = View.VISIBLE
        } else {
            binding.btnStart.visibility = View.VISIBLE
            binding.viewActions.visibility = View.GONE
        }
    }
}