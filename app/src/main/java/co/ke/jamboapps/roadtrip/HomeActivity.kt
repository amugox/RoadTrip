package co.ke.jamboapps.roadtrip

import android.os.Bundle
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import co.ke.jamboapps.roadtrip.fragment.AlertsFragment
import co.ke.jamboapps.roadtrip.fragment.HomeFragment
import co.ke.jamboapps.roadtrip.fragment.MyRoutesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity() {

    private lateinit var navButton: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navButton = findViewById(R.id.bottomNav)

        if (savedInstanceState == null) {
            if (findViewById<FrameLayout>(R.id.fragment) != null) {
                val fragment: Fragment = HomeFragment()
                supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
            }
        }

        navButton.setOnItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.nav_home -> {
                    fragment = HomeFragment()
                }
                R.id.nav_trips -> {
                    fragment = MyRoutesFragment()
                }
                R.id.nav_alerts -> {
                    fragment = AlertsFragment()
                }
            }

            if (fragment != null) {
                changeFragment(fragment, false)
                true
            } else
                false

        }
    }

}