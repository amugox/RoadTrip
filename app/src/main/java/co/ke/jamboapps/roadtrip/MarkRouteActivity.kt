package co.ke.jamboapps.roadtrip

import android.os.Bundle
import android.widget.FrameLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import co.ke.jamboapps.roadtrip.databinding.ActivityMarkRouteBinding
import co.ke.jamboapps.roadtrip.fragment.HomeFragment
import co.ke.jamboapps.roadtrip.fragment.MarkRouteFragment

class MarkRouteActivity : BaseActivity() {

    private lateinit var binding: ActivityMarkRouteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarkRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupHomeButton()

        val routeCode = intent.getIntExtra("rt_code", 0)

        if (savedInstanceState == null) {
            if (findViewById<FrameLayout>(R.id.fragment) != null) {
                val bundle = Bundle().apply { putInt("rt_code", routeCode) }
                val fragment: Fragment = MarkRouteFragment().apply { arguments = bundle }
                supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
            }
        }

    }
}