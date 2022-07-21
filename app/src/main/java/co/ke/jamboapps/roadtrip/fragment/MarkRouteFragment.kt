package co.ke.jamboapps.roadtrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import co.ke.jamboapps.roadtrip.R
import co.ke.jamboapps.roadtrip.adapter.MenuAdapter
import co.ke.jamboapps.roadtrip.databinding.FragmentMarkRouteBinding
import co.ke.jamboapps.roadtrip.listener.ListItemClickListener
import co.ke.jamboapps.roadtrip.model.ItemStat
import co.ke.jamboapps.roadtrip.model.MenuItem
import co.ke.jamboapps.roadtrip.model.StatusType
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

        var comingSoonStat = ItemStat(
            title = "Coming Soon",
            status = StatusType.WARNING
        )
        val menus = mutableListOf<MenuItem>()
        menus.add(
            MenuItem(
                title = "Manual",
                details = "Mark roure points manually with route assist as you drive.",
                icon = FontAwesome.Icon.faw_pen_alt,
                code = 0,
            )
        )
        menus.add(
            MenuItem(
                title = "Automatic",
                details = "Automatically mark the points as you drive on the route.",
                iconRes = R.drawable.ic_auto_mark,
                code = 1,
                status = comingSoonStat,
            )
        )
        menus.add(
            MenuItem(
                title = "From Map",
                details = "Select route points from map",
                icon = FontAwesome.Icon.faw_map,
                code = 2,
                status = comingSoonStat,
            )
        )

        val adapter = MenuAdapter(menus, object : ListItemClickListener {
            override fun onItemClick(v: View, position: Int, isLong: Boolean) {
                val action = menus[position].code
                baseListener!!.onAction(action, requireArguments())
            }
        })

        binding.rvMenus.adapter = adapter
    }
}