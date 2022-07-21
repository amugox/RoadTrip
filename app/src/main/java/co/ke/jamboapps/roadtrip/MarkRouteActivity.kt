package co.ke.jamboapps.roadtrip

import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import co.ke.jamboapps.roadtrip.databinding.ActivityMarkRouteBinding
import co.ke.jamboapps.roadtrip.fragment.*

class MarkRouteActivity : BaseActivity(), BaseFragment.OnActionListener {

    private lateinit var binding: ActivityMarkRouteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupHomeButton()

        val routeCode = intent.getIntExtra("rt_code", 0)

        if (savedInstanceState == null) {
            val fId = binding.contentView.fragment.id
            val bundle = Bundle().apply { putInt("rt_code", routeCode) }
            val fragment: Fragment = MarkRouteFragment().apply { arguments = bundle }
            supportFragmentManager.beginTransaction().add(fId, fragment).commit()
        }
    }

    override fun onAction(action: Int, data: Bundle) {
        val f = when (action) {
            0 -> MarkRouteManualFragment().apply { arguments = data }
//            1 -> MarkRouteAutoFragment().apply { arguments = data }
//            2 -> MarkRouteMapFragment().apply { arguments = data }
            else -> null
        }
        f?.let {
            val fId = binding.contentView.fragment.id
            changeFragment(fId, f, false)
        }
    }
}