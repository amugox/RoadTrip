package co.ke.jamboapps.roadtrip

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import co.ke.jamboapps.roadtrip.adapter.MenuAdapter
import co.ke.jamboapps.roadtrip.adapter.RouteAdapter
import co.ke.jamboapps.roadtrip.databinding.ActivityRouteBinding
import co.ke.jamboapps.roadtrip.db.MyRoute
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.model.MenuItem
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome
import java.text.SimpleDateFormat
import java.util.*

class RouteActivity : BaseActivity() {

    private lateinit var binding: ActivityRouteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupHomeButton()

        val routeCode = intent.getIntExtra("rt_code", 0)
        loadData(routeCode)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.route_menu, menu)
        return true
    }

    private fun loadData(code: Int) {
        val route = MyRoute.get(code) ?: return

        //---- Create layout manager
        val mLayoutManager = LinearLayoutManager(this)

        with(binding.contentView) {
            tvName.text = route.routeName
            tvDetails.text = route.details
            tvRouteId.text = route.routeId

            if (route.createdOn != null) {
                val f = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                tvExtras.text = getString(R.string.tv_route_extras, f.format(route.createdOn!!), "")
            }

            rvMenus.layoutManager = mLayoutManager
            rvMenus.itemAnimator = DefaultItemAnimator()

            val menus = mutableListOf<MenuItem>()
            if (route.myRoute == 1)
                menus.add(
                    MenuItem(
                        title = "Mark Route",
                        icon = FontAwesome.Icon.faw_map_marked,
                        code = 1
                    )
                )
            menus.add(MenuItem(title = "Navigate", icon = FontAwesome.Icon.faw_route, code = 2))
            menus.add(MenuItem(title = "Share", icon = FontAwesome.Icon.faw_share_alt, code = 3))

            val adapter = MenuAdapter(menus, object : ListItemClickListener {
                override fun onItemClick(v: View, position: Int, isLong: Boolean) {
                    val intent: Intent? = when (menus[position].code) {
                        1 -> Intent(applicationContext, MarkRouteActivity::class.java).apply {
                            putExtra("rt_code", route.code)
                        }
                        2 -> {
                            null
                        }
                        3 -> {
                            null
                        }
                        else -> null
                    }

                    intent?.let { startActivity(it) }
                }
            })

            rvMenus.adapter = adapter
        }
    }
}