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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import co.ke.jamboapps.roadtrip.*
import co.ke.jamboapps.roadtrip.adapter.RouteAdapter
import co.ke.jamboapps.roadtrip.custom.EmptyRecyclerView
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.service.MyLocationService

class HomeFragment : BaseFragment() {

    private var routes = mutableListOf<MyRoute>()
    private var adapter: RouteAdapter? = null
    private lateinit var rvList: EmptyRecyclerView

    private val routeResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                loadList()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<View>(R.id.cvCreate).setOnClickListener {
            val intent = Intent(requireActivity(), CreateRouteActivity::class.java)
//            startActivity(intent)
            routeResult.launch(intent)
        }

        view.findViewById<View>(R.id.cvGet).setOnClickListener {
            val intent = Intent(requireActivity(), GetRouteActivity::class.java)
            //startActivity(intent)
            routeResult.launch(intent)
        }

        rvList = view.findViewById(R.id.rvItems)

        //---- Create layout manager
        val mLayoutManager = LinearLayoutManager(requireActivity())
        rvList.layoutManager = mLayoutManager
        rvList.itemAnimator = DefaultItemAnimator()
        rvList.setEmptyView(view.findViewById(R.id.emptyListView))

        adapter = RouteAdapter(routes, object : ListItemClickListener {
            override fun onItemClick(v: View, position: Int, isLong: Boolean) {
                val route = routes[position]
                val intent = Intent(requireActivity(), RouteActivity::class.java)
                intent.putExtra("rt_code", route.code)
                routeResult.launch(intent)
            }
        })
        rvList.adapter = adapter
        loadList()

//        view.findViewById<Button>(R.id.btnStart).setOnClickListener {
//            val ctx = requireActivity().applicationContext
//            ContextCompat.startForegroundService(ctx, Intent(ctx, MyLocationService::class.java))
//        }
//        view.findViewById<Button>(R.id.btnStop).setOnClickListener {
//            requireActivity().stopService(
//                Intent(
//                    requireActivity().applicationContext,
//                    MyLocationService::class.java
//                )
//            )
//        }
//        view.findViewById<Button>(R.id.btnNav).setOnClickListener {
//            val intent = Intent(requireActivity().applicationContext, RoadMapActivity::class.java)
//            startActivity(intent)
//        }

//        if (!checkPermission()) {
//            requestPermission()
//        }

        return view
    }

    private fun loadList() {
        val myData = MyRoute.getList()
        routes.clear()
        routes.addAll(myData)
        adapter!!.notifyDataSetChanged()
    }

//    private fun checkPermission(): Boolean {
//        val ctx = requireActivity().applicationContext
//        val result =
//            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
//        val result1 =
//            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
//        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ),
//            1
//        )
//    }
}