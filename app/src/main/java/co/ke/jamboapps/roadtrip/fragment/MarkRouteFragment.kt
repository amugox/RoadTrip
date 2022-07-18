package co.ke.jamboapps.roadtrip.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import co.ke.jamboapps.roadtrip.MarkRouteActivity
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.adapter.MenuAdapter
import co.ke.jamboapps.roadtrip.databinding.FragmentMarkRouteBinding
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.model.MenuItem
import com.mikepenz.iconics.typeface.library.fontawesome.FontAwesome

class MarkRouteFragment : BaseFragment() {

    private var _binding: FragmentMarkRouteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkRouteBinding.inflate(inflater, container, false)

        loadOptions()
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadOptions() {
        //---- Create layout manager
        val mLayoutManager = LinearLayoutManager(requireActivity())

        binding.rvMenus.layoutManager = mLayoutManager
        binding.rvMenus.itemAnimator = DefaultItemAnimator()

        val menus = mutableListOf<MenuItem>()
        menus.add(MenuItem(title = "Automatic", icon = FontAwesome.Icon.faw_route, code = 1))
        menus.add(MenuItem(title = "From Map", icon = FontAwesome.Icon.faw_share_alt, code = 2))

        val adapter = MenuAdapter(menus, object : ListItemClickListener {
            override fun onItemClick(v: View, position: Int, isLong: Boolean) {
                val intent: Intent? = when (menus[position].code) {
                    1 -> null
                    2 -> null
                    else -> null
                }

                intent?.let { startActivity(it) }
            }
        })

        binding.rvMenus.adapter = adapter
    }
}