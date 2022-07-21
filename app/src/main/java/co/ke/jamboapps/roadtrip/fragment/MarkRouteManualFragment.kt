package co.ke.jamboapps.roadtrip.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.adapter.MenuAdapter
import co.ke.jamboapps.roadtrip.databinding.FragmentMarkRouteManualBinding
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.db.RouteMark
import co.ke.jamboapps.roadtrip.dialog.AddRouteMarkDialog
import co.ke.jamboapps.roadtrip.dialog.Alerts
import co.ke.jamboapps.roadtrip.dialog.WaitDialog
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.model.MenuItem
import co.ke.jamboapps.roadtrip.service.RouteMarkService
import co.ke.jamboapps.roadtrip.util.RouteUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class MarkRouteManualFragment : BaseFragment() {

    private var _binding: FragmentMarkRouteManualBinding? = null
    private val binding get() = _binding!!

    private var fusedLocationClient: FusedLocationProviderClient? = null
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
    private var routeCode = 0
    private var wd: WaitDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMarkRouteManualBinding.inflate(inflater, container, false)

        wd = WaitDialog(requireActivity())

        routeCode = requireArguments().getInt("rt_code", 0)

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

        binding.btnEnd.setOnClickListener {
            Alerts.showConfirmMessage(
                requireActivity(),
                R.string.dg_confirm_end_route_mark,
                object : Alerts.AlertActionListener {
                    override fun onPositiveAction() {
                        endRouteMarking()
                    }
                })
        }
        binding.btnCancel.setOnClickListener {
            Alerts.showConfirmMessage(
                requireActivity(),
                R.string.dg_confirm_cancel_route_mark,
                object : Alerts.AlertActionListener {
                    override fun onPositiveAction() {
                        cancelRouteMarking()
                    }
                })
        }

        loadMainMenus()

        return binding.root
    }

    private fun loadMainMenus() {
        val lmMenus: RecyclerView.LayoutManager = GridLayoutManager(requireActivity(), 3)
        binding.rvItems.layoutManager = lmMenus

        val myMenus = mutableListOf<MenuItem>()
        RouteUtil.getAssistData().forEach { item ->
            myMenus.add(
                MenuItem(
                    code = item.code,
                    title = item.title,
                    iconRes = item.iconRes,
                    icon = item.icon
                )
            )
        }

        val adapter = MenuAdapter(myMenus, object :
            ListItemClickListener {
            override fun onItemClick(v: View, position: Int, isLong: Boolean) {
                onMainMenuSelect(myMenus[position]);
            }
        }, 1)
        binding.rvItems.adapter = adapter
    }

    private fun startMarking() {
        if (fusedLocationClient == null)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
            }
        }

        showHideViews(0)
    }

    private fun endRouteMarking() {
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
            }
        }
        showHideViews(1)
    }

    private fun cancelRouteMarking() {
        //--- Clear marked data
        RouteMark.clearMarks(routeCode)
        showHideViews(1)
    }

    private fun onMainMenuSelect(menu: MenuItem) {
        Log.e("Menu", menu.title)
        wd?.show()
        //-- Get current location
        fusedLocationClient?.let {
            val priority = Priority.PRIORITY_HIGH_ACCURACY
            val tokenSource = CancellationTokenSource()
            it.getCurrentLocation(priority, tokenSource.token)
                .addOnSuccessListener { location: Location? ->
                    wd?.dismiss()
                    Log.e("Location", "location is found: $location")
                    if (location != null) {
                        AddRouteMarkDialog.show(
                            requireActivity(),
                            menu,
                            object : AddRouteMarkDialog.ActionListener {
                                override fun onConfirm() {
                                    val mark = RouteMark(
                                        code = routeCode,
                                        lat = location.latitude,
                                        lon = location.longitude,
                                        assistCode = menu.code
                                    )
                                    mark.save()
                                }

                                override fun onCancel() {}
                            })
                    }
                }
                .addOnFailureListener { exception ->
                    wd?.dismiss()
                    Log.e("Location", "Oops location failed with exception: $exception")
                }
        }
    }

    private fun showHideViews(flag: Int) {
        if (flag == 0) {
            binding.btnStart.visibility = View.GONE
            binding.viewActions.visibility = View.VISIBLE
            binding.cvAssist.visibility = View.VISIBLE
        } else {
            binding.btnStart.visibility = View.VISIBLE
            binding.viewActions.visibility = View.GONE
            binding.cvAssist.visibility = View.GONE
        }
    }
}